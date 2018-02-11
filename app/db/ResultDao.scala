package db

import java.sql.ResultSet
import javax.inject.Inject

import db.queryBuilder.{Predicate, SqlComparators, Table}
import models.Result
import play.api.db._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ResultDao @Inject()(
  override val db: Database,
  sessionDao: SessionDao,
  competitorsDao: CompetitorDao
) extends DaoTrait {

  type T = Result

  override val table = Table(
    "results",
    "r",
    Seq("id", "position", "class_position", "class_car", "car_number", "competitor_id", "start_position",
      "interval_time", "laps_led", "average_lap", "fastest_lap", "fastest_lap_number",
      "total_laps", "incidents", "club", "points", "bonus_points", "penalty_points", "final_points", "session_id"),
    Map(
      "session_id" -> sessionDao.table,
      "competitor_id" -> competitorsDao.table
    )
  )

  override def getColumnMapping(result: Result): Map[String, Any] = {
    Map(
      "id" -> result.id,
      "position" -> result.position,
      "class_position" -> result.classPosition,
      "class_car" -> result.classCar,
      "car_number" -> result.carNumber,
      "competitor_id" -> result.competitor.id,
      "start_position" -> result.startPosition,
      "interval_time" -> result.interval,
      "laps_led" -> result.lapsLed,
      "average_lap" -> result.averageLap,
      "fastest_lap" -> result.fastestLap,
      "fastest_lap_number" -> result.fastestLapNumber,
      "total_laps" -> result.totalLaps,
      "incidents" -> result.incidents,
      "club" -> result.club,
      "points" -> result.points,
      "bonus_points" -> result.bonusPoints,
      "penalty_points" -> result.penaltyPoints,
      "final_points" -> result.finalPoints,
      "session_id" -> result.sessionId
    )
  }

  override def resultSetToModel(resultSet: ResultSet) : Result = {
    Result(
      resultSet.getInt(table.alias + ".id"),
      resultSet.getInt(table.alias + ".position"),
      resultSet.getInt(table.alias + ".class_position"),
      resultSet.getString(table.alias + ".class_car"),
      resultSet.getString(table.alias + ".car_number"),
      competitorsDao.resultSetToModel(resultSet),
      resultSet.getInt(table.alias + ".start_position"),
      resultSet.getString(table.alias + ".interval_time"),
      resultSet.getInt(table.alias + ".laps_led"),
      resultSet.getString(table.alias + ".average_lap"),
      resultSet.getString(table.alias + ".fastest_lap"),
      resultSet.getString(table.alias + ".fastest_lap_number"),
      resultSet.getInt(table.alias + ".total_laps"),
      resultSet.getInt(table.alias + ".incidents"),
      resultSet.getString(table.alias + ".club"),
      resultSet.getInt(table.alias + ".points"),
      resultSet.getInt(table.alias + ".bonus_points"),
      resultSet.getInt(table.alias + ".penalty_points"),
      resultSet.getInt(table.alias + ".final_points"),
      resultSet.getInt(table.alias + ".session_id")
    )
  }

  def insertList(data: Seq[Result]) : Future[Seq[Result]] = {
    Future {
      delete(Predicate("session_id", data.head.sessionId, SqlComparators.EQUALS, table))
      insertMany(data.map(result => getColumnMapping(result)))
      data
    }
  }

  def getForSession(sessionId: Int) : Future[Seq[Result]] = {
    getWhere(Predicate("session_id", sessionId, SqlComparators.EQUALS, table))
  }

}
