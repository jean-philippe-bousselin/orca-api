package services

import javax.inject.Inject

import db._
import models._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TeamService @Inject()(
  teamDao: TeamDao
) {

  def add(team: Team) : Future[Team] = {
    teamDao.add(team)
  }

}