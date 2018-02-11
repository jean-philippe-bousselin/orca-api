package models

import play.api.libs.json.Json

case class Competitor(
  driver: Driver,
  championshipId: Int,
  team: Team,
  category: Category
) {}

object Competitor {
  implicit val format = Json.format[Competitor]

  val DEFAULT_TEAM = Team.TEAM_PRIVATEERS_ID
  val DEFAULT_CATEGORY = Category.OVERALL_CATEGORY_ID

  def getEmpty() : Competitor = Competitor(
    Driver(0, ""),
    0,
    Team(0, ""),
    Category(0, "")
  )

}
