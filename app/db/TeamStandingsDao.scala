package db

import java.sql.ResultSet
import javax.inject.Inject

import db.queryBuilder.{Predicate, SqlComparators, Table}
import models.{Standings, TeamStandings}
import play.api.db._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TeamStandingsDao @Inject()(
  override val db: Database,
  teamDao: TeamDao
) extends DaoTrait with ChampionshipIdMapping {

  type T = TeamStandings

  override val orderByDefaultColumns = Seq("position")

  override val table = Table(
    "team_standings",
    "tsta",
    Seq(
      "id", "position", "team_id", "behind_next", "behind_leader", "starts", "wins",
      "poles", "top5s", "top10s", "points", "bonus_points", "penalty_points", "laps",
      "incidents", "championship_id"
    ),
    Map(
      "team_id" -> teamDao.table
    )
  )

  override def getColumnMapping(standings: TeamStandings): Map[String, Any] = {
    Map(
      "position" -> standings.position,
      "team_id" -> standings.team.id,
      "behind_next" -> standings.behindNext,
      "behind_leader" -> standings.behindLeader,
      "starts" -> standings.starts,
      "wins" -> standings.wins,
      "poles" -> standings.poles,
      "top5s" -> standings.top5s,
      "top10s" -> standings.top10s,
      "points" -> standings.points,
      "bonus_points" -> standings.bonusPoints,
      "penalty_points" -> standings.penaltyPoints,
      "laps" -> standings.laps,
      "incidents" -> standings.incidents
    )
  }

  override def resultSetToModel(resultSet: ResultSet) : TeamStandings = {
    TeamStandings(
      resultSet.getInt(table.alias + ".id"),
      resultSet.getInt(table.alias + ".position"),
      teamDao.resultSetToModel(resultSet),
      resultSet.getInt(table.alias + ".behind_next"),
      resultSet.getInt(table.alias + ".behind_leader"),
      resultSet.getInt(table.alias + ".starts"),
      resultSet.getInt(table.alias + ".wins"),
      resultSet.getInt(table.alias + ".poles"),
      resultSet.getInt(table.alias + ".top5s"),
      resultSet.getInt(table.alias + ".top10s"),
      resultSet.getInt(table.alias + ".points"),
      resultSet.getInt(table.alias + ".bonus_points"),
      resultSet.getInt(table.alias + ".penalty_points"),
      resultSet.getInt(table.alias + ".laps"),
      resultSet.getInt(table.alias + ".incidents")
    )
  }

  def getForChampionship(id: Int) : Future[Seq[TeamStandings]] = {
    getWhere(Predicate("championship_id", id, SqlComparators.EQUALS, table))
  }

  def insertList(championshipId: Int, data: Seq[TeamStandings]) : Future[Seq[TeamStandings]] = {
    Future {
      delete(Predicate("championship_id", championshipId, SqlComparators.EQUALS, table))
      insertMany(data.map(standing => getColumnMapping(standing) ++ championshipIdMapping(championshipId)))
      data
    }
  }

}
