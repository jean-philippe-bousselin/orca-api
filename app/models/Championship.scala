package models

import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.{Json, OWrites}

case class Championship(
  id: Int = 0,
  name: String,
  description : Option[String]
) {}

object Championship {

  implicit val configWrites: OWrites[Championship] = Json.writes[Championship]

  val form: Form[Championship] = Form(
    mapping(
      "id" -> ignored(0),
      "name" -> nonEmptyText,
      "description" -> optional(text)
    )(Championship.apply)(Championship.unapply)
  )

}