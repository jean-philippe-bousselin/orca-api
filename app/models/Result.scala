package models

import play.api.libs.json._

case class Result(
  id: Int,
  position: Int,
  classPosition: Int,
  classCar: String,
  carNumber: String,
  competitor: Competitor,
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
  bonusPoints: Int,
  penaltyPoints: Int,
  finalPoints: Int,
  sessionId: Int
) {}

object Result {
  implicit val format = Json.format[Result]
}