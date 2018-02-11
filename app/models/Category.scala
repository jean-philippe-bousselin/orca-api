package models

import play.api.libs.json.Json

case class Category(
  id: Int,
  name: String
) {}

object Category {
  implicit val format = Json.format[Category]

  val OVERALL_CATEGORY_ID = 1
}
