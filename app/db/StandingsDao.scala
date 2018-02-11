package db

import java.sql.ResultSet
import javax.inject.Inject

import db.queryBuilder.{Predicate, SqlComparators, Table}
import models.{Result, Standings}
import play.api.db._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class StandingsDao @Inject()(
  override val db: Database,
  competitorsDao: CompetitorDao
) extends DaoTrait with ChampionshipIdMapping {

  type T = Standings

  override val orderByDefaultColumns = Seq("position")

  override val table = Table(
    "standings",
    "sta",
    Seq(
      "id", "position", "competitor_id", "behind_next", "bonus_points", "penalty_points", "starts", "wins",
      "poles", "top5s", "top10s", "incidents", "corners", "inc_per_race", "inc_per_lap", "inc_per_corner",
      "points", "championship_id"
    ),
    Map(
      "competitor_id" -> competitorsDao.table
    )
  )

  override def getColumnMapping(standings: Standings): Map[String, Any] = {
    Map(
      "position" -> standings.position,
      "competitor_id" -> standings.competitor.id,
      "behind_next" -> standings.behindNext,
      "bonus_points" -> standings.bonusPoints,
      "penalty_points" -> standings.penaltyPoints,
      "starts" -> standings.starts,
      "wins" -> standings.wins,
      "poles" -> standings.poles,
      "top5s" -> standings.top5s,
      "top10s" -> standings.top10s,
      "incidents" -> standings.incidents,
      "corners" -> standings.corners,
      "inc_per_race" -> standings.incPerRace,
      "inc_per_lap" -> standings.incPerLap,
      "inc_per_corner" -> standings.incPerCorner,
      "points" -> standings.points
    )
  }

  override def resultSetToModel(resultSet: ResultSet) : Standings = {
    Standings(
      resultSet.getInt(table.alias + ".id"),
      resultSet.getInt(table.alias + ".position"),
      competitorsDao.resultSetToModel(resultSet),
      resultSet.getInt(table.alias + ".behind_next"),
      resultSet.getInt(table.alias + ".bonus_points"),
      resultSet.getInt(table.alias + ".penalty_points"),
      resultSet.getInt(table.alias + ".starts"),
      resultSet.getInt(table.alias + ".wins"),
      resultSet.getInt(table.alias + ".poles"),
      resultSet.getInt(table.alias + ".top5s"),
      resultSet.getInt(table.alias + ".top10s"),
      resultSet.getInt(table.alias + ".incidents"),
      resultSet.getInt(table.alias + ".corners"),
      resultSet.getDouble(table.alias + ".inc_per_race"),
      resultSet.getDouble(table.alias + ".inc_per_lap"),
      resultSet.getDouble(table.alias + ".inc_per_corner"),
      resultSet.getInt(table.alias + ".points")
    )
  }

  def getForChampionship(id: Int) : Future[Seq[Standings]] = {
    getWhere(Predicate("championship_id", id, SqlComparators.EQUALS, table))
  }

  def insertList(championshipId: Int, data: Seq[Standings]) : Future[Seq[Standings]] = {
    Future {
      delete(Predicate("championship_id", championshipId, SqlComparators.EQUALS, table))
      insertMany(data.map(standing => getColumnMapping(standing) ++ championshipIdMapping(championshipId)))
      data
    }
  }

}
