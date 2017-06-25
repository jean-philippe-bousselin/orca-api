package models

import play.api.data._
import play.api.data.Forms._
import play.api.libs.json.{Json, OWrites}

case class ChampionshipConfiguration(
  sessionTypes: Seq[SessionType],
  subClasses: Option[Seq[String]]
) { }

object ChampionshipConfiguration {

  implicit val writes: OWrites[ChampionshipConfiguration] = Json.writes[ChampionshipConfiguration]

  val form: Form[ChampionshipConfiguration] = Form(
    mapping(
      "sessionTypes" -> seq(SessionType.getMapping()),
      "subClasses" -> optional(seq(text))
    )(ChampionshipConfiguration.apply)(ChampionshipConfiguration.unapply)
  )
}
