package db

import java.sql.ResultSet
import javax.inject.Inject

import db.queryBuilder.Table
import models.{Championship, ChampionshipConfiguration, SessionType}
import play.api.db._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SessionTypeDao @Inject()(
  override val db: Database
) extends DaoTrait with ChampionshipIdMapping {

  type T = SessionType

  override val table = Table(
    "session_type",
    "st",
    Seq("id", "name", "points", "incidents_limit", "penalty_points", "bonus_points", "championship_id")
  )

  override def getColumnMapping(sessionType: SessionType) : Map[String, Any] = {
    Map(
      "name" -> sessionType.name,
      "points" -> sessionType.points.mkString(","),
      "incidents_limit" -> sessionType.incidentsLimit,
      "penalty_points" -> sessionType.penaltyPoints,
      "bonus_points" -> sessionType.bonusPoints
    )
  }

  override def resultSetToModel(resultSet: ResultSet) : SessionType = {
    SessionType(
      resultSet.getInt(table.alias + ".id"),
      resultSet.getString(table.alias + ".name"),
      resultSet.getString(table.alias + ".points").split(",").map(_.toInt),
      resultSet.getInt(table.alias + ".incidents_limit"),
      resultSet.getInt(table.alias + ".penalty_points"),
      resultSet.getInt(table.alias + ".bonus_points")
    )
  }

  def add(championshipId: Int, sessionType: SessionType) : Future[SessionType] = {
    Future {
      val insertedId = insert(getColumnMapping(sessionType) ++ championshipIdMapping(championshipId))
      sessionType.copy(id = insertedId)
    }
  }

}
