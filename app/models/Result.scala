package models

import play.api.libs.json._

case class Result(
  id: String,
  position: Int,
  classPosition: Int,
  classCar: String,
  carNumber: String,
  fullName: String,
  startPosition: Int,
  interval: String,
  lapsLed: Int,
  averageLap: String,
  fastestLap: String,
  fastestLapNumber: String,
  totalLaps: Int,
  incidents: Int,
  club: String,
  points: Int,
  penaltyPoints: Int,
  sessionId: String
) {}

object Result {
  implicit val championshipWrites: OWrites[Result] = Json.writes[Result]
}
