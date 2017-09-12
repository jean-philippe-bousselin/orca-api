package models


import play.api.data._
import play.api.data.Forms.{mapping, _}
import play.api.libs.json.{Json, OWrites}
import play.api.libs.functional.syntax._

case class Driver(
  id: Int,
  name: String,
  category: String,
  team: Option[Team]
) {}


object Driver {
  implicit val writes: OWrites[Driver] = Json.writes[Driver]
  val DEFAULT_CATEGORY = "ALL"

  def getMappingWithMandatoryId(): Mapping[Driver] = {
    mapping(
      "id" -> number,
      "name" -> nonEmptyText,
      "category" -> nonEmptyText,
      "team" -> optional(Team.getMappingWithMandatoryId())
    )(Driver.apply)(Driver.unapply)
  }

  def getMapping(): Mapping[Driver] = mapping(
    "id" -> ignored(0),
    "name" -> nonEmptyText,
    "category" -> nonEmptyText,
    "team" -> optional(Team.getMappingWithMandatoryId())
  )(Driver.apply)(Driver.unapply)

  val form: Form[Driver] = Form(getMapping())
  val formWithRequiredId: Form[Driver] = Form(getMappingWithMandatoryId())

}



