package services

import javax.inject.Inject

import db.ChampionshipDao
import models.{Championship, ChampionshipConfiguration, Result, Session}
import play.api.libs.json.{JsValue, Json}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChampionshipsService @Inject()(
  championshipDao: ChampionshipDao
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

  def configure(id: Int, configuration: ChampionshipConfiguration) : Future[Boolean] = {
    championshipDao.saveConfiguration(id, configuration)
  }

//  def getSessions(id: Int) : Future[Seq[Session]] = {
//    sessionMongo.getForChampionship(id)
//  }
//
//
//  def getResults(id: String) : Future[Seq[Result]] = {
//
//    getSessions(id).flatMap { sessions =>
//      val sessionsIds: Seq[String] = sessions.map(_.id.get)
//      resultMongo.getForSessions(sessionsIds)
//    }
//
//  }
//
//  def buildStandings(id: String) : Future[JsValue] = {
//
//    for {
//
//      results <- getResults(id)
//
//    } yield Json.toJson(results)
//
//  }

}
