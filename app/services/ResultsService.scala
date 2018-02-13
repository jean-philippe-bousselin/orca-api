package services

import models.{Driver, Result, Session, SessionType, Competitor}
import db.{CompetitorDao, DriverDao, ResultDao, SessionDao}
import java.io.File
import javax.inject.Inject

import _root_.net.ruippeixotog.scalascraper.dsl.DSL._
import _root_.net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import _root_.net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import _root_.net.ruippeixotog.scalascraper.model._
import _root_.net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import play.api.Logger
import play.api.libs.Files
import play.api.libs.json.Json
import play.api.mvc.MultipartFormData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.io.Source


class ResultsService @Inject()(
  sessionsDao: SessionDao,
  resultDao: ResultDao,
  driverDao: DriverDao,
  competitorDao: CompetitorDao,
  championshipsDao: ChampionshipsService,
  championshipsService: ChampionshipsService
){

  def getForSession(sessionId: Int) : Future[Seq[Result]] = {
    resultDao.getForSession(sessionId)
  }

  def uploadExtractAndLoadResults(
    sessionId: Int,
    files: Seq[MultipartFormData.FilePart[Files.TemporaryFile]]
  ) : Future[Seq[Seq[Result]]] = {

    sessionsDao.find(sessionId).flatMap {
      case Some(session) => {
        process(session, files)
      }
      case None => throw new NoSuchElementException(s"Session ${sessionId} not found")
    }

  }

  private def process(
    session: Session,
    files: Seq[MultipartFormData.FilePart[Files.TemporaryFile]]
  ) : Future[Seq[Seq[Result]]] = {

    val browser: Browser = JsoupBrowser()

    Future.sequence(
      files.map { file =>
        for {
          championshipId     <- sessionsDao.getChampionshipId(session.id)
          extractedLines     <- extractRawCSVResults(file.ref.file)//(browser)
          transformedResults <- transformAsResults(championshipId, session.id, extractedLines)
          finalResults       <- calculatePointsAndPenalties(session.sessionType, transformedResults)
          insertedResults    <- resultDao.insertList(finalResults)
        } yield insertedResults
      }
    )
  }


  private def calculatePointsAndPenalties(
    sessionType: SessionType,
    results: Seq[Result]
  ) : Future[Seq[Result]] = Future {

    results
      .filter(_.totalLaps > 0) // filter out drivers that did not race
      .map { result =>
      val points = result.position match {
        case pos if pos < sessionType.points.length => sessionType.points(pos - 1)
        case _ => 0
      }
      if(sessionType.incidentsLimit != 0) {
        val penaltyPoints = (result.incidents / sessionType.incidentsLimit) * sessionType.penaltyPoints
        val bonusPoints = result.incidents match {
          case 0 => sessionType.bonusPoints
          case _ => 0
        }
        result.copy(
          points = points,
          bonusPoints = bonusPoints,
          penaltyPoints = penaltyPoints,
          finalPoints = points - penaltyPoints + bonusPoints
        )
      } else {
        result
      }
    }

  }


  private def extractRawHTMLResults(
    file: File
  )(implicit browser: Browser) : Future[Seq[List[String]]] = Future {
    val doc = browser.parseFile(file)
    val items: Seq[Element] = doc >> elementList(".single-results-container.race tr")
    items
      .drop(4)
      .map(_ >> elementList("td"))
      .map(_ >> allText)
  }

  private def extractRawCSVResults(file: File): Future[Seq[Seq[String]]] = Future {
    val bufferedSource = Source.fromFile(file)
    val extractedResults: Seq[Seq[String]] = bufferedSource.getLines.drop(9).foldLeft(Seq[Seq[String]]())(
      (acc, line) => {
        acc :+ line.split(",").map(_.replaceAll("\"", "")).map(_.trim).toSeq
      }
    )
    bufferedSource.close

    Logger.info(extractedResults.toString())

    extractedResults
  }


  private def transformAsResults(championshipId: Int, sessionId: Int, rawResults: Seq[Seq[String]]) : Future[Seq[Result]] = {
    def getCompetitor(driverName: String) : Future[Competitor] = {
      competitorDao.createIfNotExists(driverName, championshipId)
    }

    Future.sequence(
      rawResults.map { resultLine =>
        getCompetitor(resultLine(7).toString).map { competitor =>
          lineAsResult(resultLine).copy(
            sessionId = sessionId,
            competitor = competitor
          )
        }
      }
    )

  }

  private def lineAsResult(line: Seq[String]) : Result = {

    Result(
      0, // empty id
      line.head.toString.toInt, // position
      0, // class position - not in the CSV file
      line(2).toString, // classCar
      line(9).toString, // carNumber
      Competitor.getEmpty(), // competitor
      line(8).toString.toInt, // start position
      line(12).toString, // interval
      line(13).toString.toInt, // lapsLed
      line(15).toString, // averageLap
      line(16).toString, // fastestLap
      line(17).toString, // fastestLapNumber
      line(18).toString.toInt, // totalLaps
      line(19).toString.toInt, // incidents
      "", // club - not in csv file
      0, // 0 points
      0, // 0 bonus points
      0, // 0 penalty points
      0, // 0 final points
      0 // empty session id
    )
  }

}
