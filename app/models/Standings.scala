package models

import play.api.libs.json.{Json, OWrites}

case class Standings(
  id: Int,
  position: Int,
  driver: Driver,
  behindNext: Int,
  bonusPoints: Int,
  penaltyPoints: Int,
  starts: Int,
  wins: Int,
  poles: Int,
  top5s: Int,
  top10s: Int,
  incidents: Int,
  corners: Int,
  incPerRace: Double,
  incPerLap: Double,
  incPerCorner: Double,
  points : Int
) {

  def updateWithResult(result: Result) : Standings = {
    // @TODO bonus points
    copy(
      penaltyPoints = penaltyPoints + result.penaltyPoints,
      bonusPoints = bonusPoints + result.bonusPoints,
      wins = wins + (if(result.position == 1) 1 else 0),
      poles = poles + (if(result.startPosition == 1) 1 else 0),
      top5s = top5s + (if(result.position <= 5) 1 else 0),
      top10s = top10s + (if(result.position <= 10) 1 else 0),
      incidents = incidents + result.incidents,
      points = points + result.finalPoints
    )
  }

}

object Standings {
  implicit val writes: OWrites[Standings] = Json.writes[Standings]

  def generateFromResult(result : Result) : Standings = {
    Standings(
      0, // id
      result.position,
      result.driver,
      0, // beh next
      result.bonusPoints,
      result.penaltyPoints,
      1, // starts
      if(result.position == 1) 1 else 0, // wins
      if(result.startPosition == 1) 1 else 0, // poles
      if(result.position <= 5) 1 else 0, // poles
      if(result.position <= 10) 1 else 0, // poles
      result.incidents,
      0, // @TODO corner to add
      0.0,
      0.0,
      0.0,
      result.finalPoints
    )
  }

}



