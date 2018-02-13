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
                                      driverStandingsDao: StandingsDao,
                                      sessionDao: SessionDao,
                                      resultDao: ResultDao,
                                      driverDao: DriverDao,
                                      teamDao: TeamDao,
                                      teamStandingsDao: TeamStandingsDao
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

  def getDriverStandings(id: Int) : Future[Seq[Standings]] = {
    driverStandingsDao.getForChampionship(id)
  }
  def getTeamStandings(id: Int) : Future[Seq[TeamStandings]] = {
    teamStandingsDao.getForChampionship(id)
  }

  def buildStandings(id: Int) : Future[Boolean] = {
    sessionDao.getAll(id).flatMap { sessionList =>
      Future.sequence(sessionList.map { session => resultDao.getForSession(session.id) })
    }.flatMap { resultsList =>
      Future.sequence(Seq(
        driverStandingsDao.insertList(id, buildDriverStandings(resultsList.flatten)),
        teamStandingsDao.insertList(id, buildTeamStandings(resultsList.flatten))
      )).map(x => true)
    }
  }

  private def buildTeamStandings(results: Seq[Result]) : Seq[TeamStandings] = {
    val standingsList: Seq[TeamStandings] = results.foldLeft(Seq[TeamStandings]())(
      (standings, result) => {
        standings
          .filter(_.team.id != Team.TEAM_PRIVATEERS_ID) // privateers do not count in the standings
          .find(s => s.team.id == result.competitor.team.id) match {
          case Some(s) => standings.updated(standings.indexOf(s),s.updateWithResult(result))
          case None => standings ++ Seq(TeamStandings.generateFromResult(result))
        }
      }
    )
    // recalculate positions according to points
    standingsList.sortWith((r1, r2) => {
      r1.points > r2.points
    }).zipWithIndex.map { resWithIndex =>resWithIndex._1.copy(position =  resWithIndex._2 + 1)}
  }

  private def buildDriverStandings(results: Seq[Result]) : Seq[Standings] = {
    val standingsList: Seq[Standings] = results.foldLeft(Seq[Standings]())(
      (standings, result) => {
        standings.find(s => s.competitor.id == result.competitor.id) match {
          case Some(s) => standings.updated(standings.indexOf(s),s.updateWithResult(result))
          case None => standings ++ Seq(Standings.generateFromResult(result))
        }
      }
    )
    // recalculate positions according to points
    standingsList.sortWith((r1, r2) => {
      r1.points > r2.points
    }).zipWithIndex.map { resWithIndex =>resWithIndex._1.copy(position =  resWithIndex._2 + 1)}
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
