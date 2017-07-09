package controllers

import javax.inject.Inject

import scala.util.Success
import scala.util.Failure
import models.Session
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller, Result}
import services.SessionsService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SessionsController @Inject()(sessionService: SessionsService) extends Controller {

  def getAll(championshipId: Int) = Action.async { implicit request =>
    sessionService.getAll(championshipId).map { sessions =>
      Ok(Json.toJson(sessions))
    }
  }

  def find(id : Int) = Action.async { implicit request =>
    sessionService.findById(id).map {
      case Some(session) => Ok(Json.toJson(session))
      case None => NotFound
    }
  }

}
