package services

import javax.inject.Inject

import db.TrackDao
import models.Track

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TracksService @Inject()(trackDao: TrackDao) {

  def add(track: Track) : Future[Track] = {
    trackDao.add(track)
  }

  def getAll() : Future[Seq[Track]] = {
    trackDao.getAll()
  }

}
