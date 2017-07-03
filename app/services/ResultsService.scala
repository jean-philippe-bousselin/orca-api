package services

import models.{Result, Session, SessionType}
import db.ResultDao
import java.io.File
import javax.inject.Inject

import _root_.net.ruippeixotog.scalascraper.dsl.DSL._
import _root_.net.ruippeixotog.scalascraper.dsl.DSL.Extract._
import _root_.net.ruippeixotog.scalascraper.dsl.DSL.Parse._
import _root_.net.ruippeixotog.scalascraper.model._
import _root_.net.ruippeixotog.scalascraper.browser.{Browser, JsoupBrowser}
import db.SessionDao
import play.api.Logger
import play.api.libs.Files
import play.api.libs.json.Json
import play.api.mvc.MultipartFormData

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class ResultsService @Inject()(
  sessionsDao: SessionDao,
  resultDao: ResultDao,
  championshipsDao: ChampionshipsService
){

//  def getForSession(sessionId: String) : Future[Seq[Result]] = {
//    resultDao.getForSession(sessionId)
//  }
//
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
          extractedLines     <- extractRawResults(file.ref.file)(browser)
          transformedResults <- transformAsResults(session.id, extractedLines)
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

    results.map { result =>
      val points = result.position match {
        case pos if pos < sessionType.points.length => sessionType.points(pos - 1)
        case _ => 0
      }
      if(sessionType.incidentsLimit != 0) {
        val penaltyPoints = (result.incidents / sessionType.incidentsLimit) * sessionType.penaltyPoints
        result.copy(
          points = points,
          penaltyPoints = penaltyPoints,
          finalPoints = points - penaltyPoints
        )
      } else {
        result
      }
    }

  }


  private def extractRawResults(
    file: File
  )(implicit browser: Browser) : Future[Seq[List[String]]] = Future {

    val doc = browser.parseFile(file)
    val items: Seq[Element] = doc >> elementList(".single-results-container.race tr")
    items
      .drop(4)
      .map(_ >> elementList("td"))
      .map(_ >> allText)
  }

  private def transformAsResults(sessionId: Int, rawResults: Seq[List[String]]) : Future[Seq[Result]] = Future {
    rawResults.map { resultLine =>
      lineAsResult(resultLine).copy(
        sessionId = sessionId
      )
    }
  }

  private def lineAsResult(line: Seq[Any]) : Result = {
    Result(
      0, // empty id
      line.head.toString.toInt,
      line(1).toString.toInt,
      line(3).toString,
      line(4).toString,
      line(5).toString,
      line(6).toString.toInt,
      line(9).toString,
      line(10).toString.toInt,
      line(11).toString,
      line(12).toString,
      line(13).toString, //
      line(14).toString.toInt,
      line(15).toString.toInt,
      line(16).toString,
      0, // 0 points
      0, // 0 penalty points
      0, // 0 final points
      0 // empty session id
    )
  }

}
