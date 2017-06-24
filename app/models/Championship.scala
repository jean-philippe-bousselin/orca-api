package models

import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.{Json, OWrites}

case class Championship(
  id: Int = 0,
  name: String,
  description : Option[String],
  configuration: ChampionshipConfiguration
) {}

object Championship {

  implicit val configWrites: OWrites[Championship] = Json.writes[Championship]

  def apply(id: Int, name: String, description: Option[String]) : Championship = {
    Championship(id, name, description, new ChampionshipConfiguration(Seq.empty))
  }

  val form: Form[Championship] = Form(
    mapping(
      "id" -> ignored(0),
      "name" -> nonEmptyText,
      "description" -> optional(text),
      //"configuration" -> ignored(ChampionshipConfiguration(Seq.empty))
      "ChampionshipConfiguration" -> mapping(
        "RaceType" -> seq(mapping(
          "name" -> text,
          "points" -> seq(number),
          "penalty" -> number,
          "incidentsLimit" -> number
        )(RaceType.apply)(RaceType.unapply))
      )(ChampionshipConfiguration.apply)(ChampionshipConfiguration.unapply)
    )(Championship.apply)(Championship.unapply)
  )

}