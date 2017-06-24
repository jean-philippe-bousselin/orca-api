package models

import play.api.libs.json.{Json, OWrites}

case class ChampionshipConfiguration(
  raceTypes: Seq[RaceType]
) {}

object ChampionshipConfiguration {
  implicit val configWrites: OWrites[ChampionshipConfiguration] = Json.writes[ChampionshipConfiguration]
}
