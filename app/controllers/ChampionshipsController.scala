package controllers

import javax.inject.Inject

import models.{Championship, ChampionshipConfiguration, Session, SessionType}
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.{ChampionshipsService, SessionsService}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChampionshipsController @Inject()(
  championshipService: ChampionshipsService,
  sessionService: SessionsService
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
    Future {
      Ok("")
    }
  }

  def configure(id: Int) = Action.async { implicit request =>
    ChampionshipConfiguration.form.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(Json.toJson(formWithErrors.toString)))
      },
      configuration => {
        championshipService.configure(id, configuration).map { isSuccess =>
          // @TODO handle failure
          NoContent
        }
      }
    )
  }

//
//  def standings(id : String) = Action.async { implicit request =>
//    championshipService.buildStandings(id).map { standings =>
//      Ok(Json.toJson(standings))
//    }
//  }
//


}
