package models

import play.api.libs.json.Json

case class TeamStandings(
  id: Int,
  position: Int,
  team: Team,
  behindNext: Int,
  behindLeader: Int,
  starts: Int,
  wins: Int,
  poles: Int,
  top5s: Int,
  top10s: Int,
  points : Int,
  bonusPoints: Int,
  penaltyPoints: Int,
  laps: Int,
  incidents: Int
) {

  def updateWithResult(result: Result) : TeamStandings = {
    copy(
      penaltyPoints = penaltyPoints + result.penaltyPoints,
      bonusPoints = bonusPoints + result.bonusPoints,
      wins = wins + (if(result.position == 1) 1 else 0),
      poles = poles + (if(result.startPosition == 1) 1 else 0),
      top5s = top5s + (if(result.position <= 5) 1 else 0),
      top10s = top10s + (if(result.position <= 10) 1 else 0),
      incidents = incidents + result.incidents,
      points = points + result.finalPoints,
      laps = laps + result.totalLaps,
      starts = starts + 1
    )
  }

}

object TeamStandings {

  implicit val format = Json.format[TeamStandings]

  def generateFromResult(result : Result) : TeamStandings = {
    TeamStandings(
      0, // id
      result.position,
      result.competitor.team,
      0, // beh next
      0, // beh leader
      1, // starts
      if(result.position == 1) 1 else 0, // wins
      if(result.startPosition == 1) 1 else 0, // poles
      if(result.position <= 5) 1 else 0, // top5s
      if(result.position <= 10) 1 else 0, // top10s
      result.finalPoints, // points
      result.bonusPoints,
      result.penaltyPoints,
      result.totalLaps,
      result.incidents
    )
  }
}
