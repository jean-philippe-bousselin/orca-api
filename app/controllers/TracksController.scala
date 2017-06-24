package controllers

import javax.inject.Inject

import models.Track
import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.TracksService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TracksController @Inject()(tracksService: TracksService) extends Controller {

  def index = Action.async { implicit request =>
    tracksService.getAll().map { tracks =>
      Ok(Json.toJson(tracks))
    }
  }

  def add = Action.async(parse.json) { implicit request =>
    Track.form.bindFromRequest.fold(
      formWithErrors => {
        Future.successful(BadRequest(Json.toJson(formWithErrors.toString)))
      },
      track => {
        tracksService.add(track).map { track =>
          Ok(Json.toJson(track))
        }
      }
    )
  }


}
