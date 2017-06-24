package services

import javax.inject.Inject

import db.{ChampionshipDao, SessionDao}
import models.Session

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SessionsService @Inject()(
 championshipDao: ChampionshipDao,
 sessionDao: SessionDao
) {

  def add(championshipId: Int, session: Session) : Future[Session] = {
    championshipDao.find(championshipId).flatMap {
      case Some(championship) => sessionDao.add(championship.id, session)
      case _ => Future.failed(new Exception("Cannot add a session to a non existing championship."))
    }
  }


  def getAll(championshipId: Int) : Future[Seq[Session]] = {
    sessionDao.getAll(championshipId: Int)
  }


  def findById(id: Int) : Future[Option[Session]] = {
    sessionDao.find(id)
  }

}
