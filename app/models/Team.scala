package models

import play.api.libs.json.{Json, OWrites}

case class Team(
  id: Int,
  name: String
) {}

object Team {
  implicit val writes: OWrites[Team] = Json.writes[Team]
}






