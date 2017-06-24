package models

import play.api.libs.json.{Json, OWrites}

case class SessionType(
  id: Int,
  name: String
) {}

object SessionType {

  implicit val writes: OWrites[SessionType] = Json.writes[SessionType]

}
