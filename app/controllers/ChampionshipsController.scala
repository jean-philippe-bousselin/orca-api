package controllers

import javax.inject.Inject

import models._
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.{ChampionshipsService, SessionsService, TeamService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChampionshipsController @Inject()(
  championshipService: ChampionshipsService,
  sessionService: SessionsService,
  teamService: TeamService
) extends Controller {

  def index = Action.async { implicit request =>
    championshipService.getAll.map { championships =>
      Ok(Json.toJson(championships))
    }
  }

  def add = Action.async(parse.json) { implicit request =>
    Championship.form.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(Json.toJson(formWithErrors.toString)))
      },
      championship => {
        championshipService.add(championship).map { championship =>
          Ok(Json.toJson(championship))
        }
      }
    )
  }

  def findById(id : Int) = Action.async { implicit request =>
    championshipService.findById(id).map {
      case Some(championship) => Ok(Json.toJson(championship))
      case None => NotFound
    }
  }

  def addSession(id : Int) = Action.async { implicit request =>
    Session.form.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(Json.toJson(formWithErrors.toString)))
      },
      session => {
        sessionService.add(id, session).map { session =>
          Ok(Json.toJson(session))
        }
      }
    )
  }

  def getSessionTypes(id: Int) = Action.async { implicit request =>
    championshipService.getConfiguration(id).map { configuration =>
      Ok(Json.toJson(configuration.sessionTypes))
    }
  }

  def configure(id: Int) = Action.async { implicit request =>
    ChampionshipConfiguration.form.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(Json.toJson(formWithErrors.errors.map(_.toString))))
      },
      configuration => {
        championshipService.configure(id, configuration).map { updatedConfig =>
          Ok(Json.toJson(updatedConfig))
        }
      }
    )
  }

  def getConfiguration(id: Int) = Action.async { implicit request =>
    championshipService.getConfiguration(id).map { configuration =>
      Ok(Json.toJson(configuration))
    }
  }

  def standings(id: Int) = Action.async { implicit request =>
    championshipService.getStandings(id).map { standings =>
      Ok(Json.toJson(standings))
    }
  }

  def drivers(id: Int) = Action.async { implicit request =>
    championshipService.drivers().map { drivers =>
      Ok(Json.toJson(drivers))
    }
  }

  def teams(id: Int) = Action.async { implicit request =>
    championshipService.teams().map { teams =>
      Ok(Json.toJson(teams))
    }
  }

  def addTeam(id: Int) = Action.async { implicit request =>
    Team.form.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(Json.toJson(formWithErrors.toString)))
      },
      team => {
        teamService.add(team).map { team =>
          Ok(Json.toJson(team))
        }
      }
    )
  }

//  def updateDriver(id: Int) = Action.async { implicit request =>
//    Driver.formWithRequiredId.bindFromRequest.fold(
//      formWithErrors => {
//        Future.successful(BadRequest(Json.toJson(formWithErrors.toString)))
//      },
//      driver => {
//        championshipService.updateDriver(driver).map { driver =>
//          Ok(Json.toJson(driver))
//        }
//      }
//    )
//  }

}
