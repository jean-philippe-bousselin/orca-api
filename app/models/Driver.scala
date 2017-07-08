package models

import play.api.libs.json.{Json, OWrites}

case class Driver(
  id: Int,
  name: String,
  team: Option[Team]
) {}


object Driver {
  implicit val writes: OWrites[Driver] = Json.writes[Driver]
}



