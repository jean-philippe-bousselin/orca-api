package models

import play.api.libs.json.Json

case class Driver(
  id: Int,
  name: String
) {}

object Driver {
  implicit val format = Json.format[Driver]
}



