package controllers

import javax.inject.Inject

import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.ResultsService

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class ResultsController @Inject()(resultsService: ResultsService) extends Controller {

//  def getForSession(sessionId: String) = Action.async { implicit request =>
//    resultsService.getForSession(sessionId).map { results =>
//      Ok(Json.toJson(results))
//    }
//  }

  def uploadResults(sessionId: Int) = Action.async(parse.multipartFormData) { request =>
    resultsService.uploadExtractAndLoadResults(sessionId, request.body.files)
      .map { results =>
        Ok(Json.toJson(results))
      }
  }

}
