package controllers


import play.api.Logger
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}

/**
  * A very small controller that renders a home page.
  */
class HomeController extends Controller {

  def options(path: String) = Action {
    Logger.debug("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA")
    Ok("").withHeaders(
      "Access-Control-Allow-Origin" -> "*",
      "Access-Control-Allow-Methods" -> "GET, POST, PUT, DELETE, OPTIONS",
      "Access-Control-Allow-Headers" -> "Accept, Origin, Content-type, X-Json, X-Prototype-Version, X-Requested-With",
      "Access-Control-Allow-Credentials" -> "true",
      "Access-Control-Max-Age" -> (60 * 60 * 24).toString
    )
  }

//  def index = Action { implicit request =>
//    Ok(views.html.index())
//  }

}
