package models

import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.{JsPath, Json, OWrites, Writes}
import play.api.libs.functional.syntax._

case class Team(
  id: Int,
  name: String
) {}

object Team {
  implicit val writes: OWrites[Team] = Json.writes[Team]

  def getMappingWithMandatoryId() : Mapping[Team] = {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText
    )(Team.apply)(Team.unapply)
  }
  def getMapping() : Mapping[Team] = {
    mapping(
      "id" -> ignored(0),
      "name" -> nonEmptyText
    )(Team.apply)(Team.unapply)
  }

  val form = Form(getMapping())
}






