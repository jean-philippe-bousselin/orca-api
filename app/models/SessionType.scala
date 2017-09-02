package models

import play.api.data._
import play.api.data.Forms.{mapping, _}
import play.api.libs.json.{Json, OWrites}

case class SessionType(
  id: Int,
  name: String,
  points: Seq[Int],
  incidentsLimit: Int,
  penaltyPoints: Int,
  bonusPoints: Int
) {}

object SessionType {

  implicit val writes: OWrites[SessionType] = Json.writes[SessionType]

  def getMappingWithMandatoryId() : Mapping[SessionType] = {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "points" -> seq(number),
      "incidentsLimit" -> number,
      "penaltyPoints" -> number,
      "bonusPoints" -> number
    )(SessionType.apply)(SessionType.unapply)
  }

  def getMapping() = mapping(
    "id" -> number,
    "name" -> nonEmptyText,
    "points" -> seq(number),
    "incidentsLimit" -> number,
    "penaltyPoints" -> number,
    "bonusPoints" -> number
  )(SessionType.apply)(SessionType.unapply)

  val form: Form[SessionType] = Form(
    getMapping()
  )
}
