package services

import javax.inject.Inject

import db._
import models._
import play.api.Logger
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChampionshipsService @Inject()(
  championshipDao: ChampionshipDao,
  standingsDao: StandingsDao,
  sessionDao: SessionDao,
  resultDao: ResultDao,
  driverDao: DriverDao,
  teamDao: TeamDao
) {

  def add(championship: Championship) : Future[Championship] = {
    championshipDao.add(championship)
  }

  def getAll() : Future[Seq[Championship]] = {
    championshipDao.getAll()
  }

  def findById(id: Int) : Future[Option[Championship]] = {
    championshipDao.find(id)
  }

  def configure(id: Int, configuration: ChampionshipConfiguration) : Future[ChampionshipConfiguration] = {
    championshipDao.saveConfiguration(id, configuration)
  }

  def getConfiguration(id: Int) : Future[ChampionshipConfiguration] = {
    championshipDao.getConfiguration(id)
  }

  def getStandings(id: Int) : Future[Seq[Standings]] = {
    standingsDao.getForChampionship(id)
  }

  def buildStandings(id: Int) : Future[Seq[Standings]] = {
    sessionDao.getAll(id).flatMap { sessionList =>
      Future.sequence(
        sessionList.map { session =>
          resultDao.getForSession(session.id)
        }
      )
    }.map { resultsList =>
      resultsList.flatten.foldLeft(Seq[Standings]())(
        (standings, result) => {
          standings.find(s => s.driver.id == result.driver.id) match {
            case Some(s) => standings.updated(standings.indexOf(s),s.updateWithResult(result))
            case None => standings ++ Seq(Standings.generateFromResult(result))
          }
        }
      )
    }.flatMap { standingsList =>
      // recalculate positions according to points
      val standingsWithUpdatedPositions = standingsList.sortWith((r1, r2) => {
        r1.points > r2.points
      }).zipWithIndex.map { resWithIndex =>resWithIndex._1.copy(position =  resWithIndex._2 + 1)}
      standingsDao.insertList(id, standingsWithUpdatedPositions)
    }
  }

  def drivers() : Future[Seq[Driver]] = {
    driverDao.getAll()
  }

  def teams() : Future[Seq[Team]] = {
    teamDao.getAll()
  }

  def updateDriver(driver: Driver) : Future[Driver] = {
    driverDao.updateDriver(driver)
  }

}
