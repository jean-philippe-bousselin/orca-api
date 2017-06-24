package models

import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.{Json, OWrites}

case class Session(
  id: Int,
  name: String,
  date: String,
  time: String,
  track: Track
) {}

object Session {

  implicit val sessionWrites: OWrites[Session] = Json.writes[Session]

  def getMapping() : Mapping[Session] = mapping(
    "id" -> ignored(0),
    "name" -> text,
    "date" -> text,
    "time" -> text,
    "track" -> Track.getMappingWithMandatoryId()
  )(Session.apply)(Session.unapply)

  val form: Form[Session] = Form(getMapping())

}

