package models

import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.{JsPath, Json, OWrites, Writes}
import play.api.libs.functional.syntax._

case class Track(
  id: Int,
  name: String,
  thumbnailUrl: Option[String]
) {}

object Track {

  implicit val writes: OWrites[Track] = Json.writes[Track]


  def getMappingWithMandatoryId() : Mapping[Track] = {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "thumbnailUrl" -> optional(text)
    )(Track.apply)(Track.unapply)
  }
  def getMapping() : Mapping[Track] = {
    mapping(
      "id" -> ignored(0),
      "name" -> nonEmptyText,
      "thumbnailUrl" -> optional(text)
    )(Track.apply)(Track.unapply)
  }

  val form = Form(getMapping())

}