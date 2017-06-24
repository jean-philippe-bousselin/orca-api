package models

import play.api.libs.json.{Json, OWrites}

case class RaceType(
  name: String,
  points: Seq[Int],
  penalty: Int,
  incidentsLimit: Int
) {}

object RaceType {
  implicit val raceTypeWrites: OWrites[RaceType] = Json.writes[RaceType]
}


