package db

import java.sql.ResultSet
import javax.inject.Inject

import models.{Championship, ChampionshipConfiguration, SessionType}
import play.api.db._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SessionTypeDao @Inject()(
  override val db: Database
) extends DaoTrait with ChampionshipIdMapping {

  type T = SessionType

  override val tableName = "session_type"

  override def getColumnMapping(sessionType: SessionType) : Map[String, Any] = {
    Map(
      "name" -> sessionType.name,
      "points" -> sessionType.points.mkString(","),
      "incidents_limit" -> sessionType.incidentsLimit,
      "penalty_points" -> sessionType.penaltyPoints
    )
  }

  override def resultSetToModel(resultSet: ResultSet) : SessionType = {
    SessionType(
      resultSet.getInt("id"),
      resultSet.getString("name"),
      resultSet.getString("points").split(",").map(_.toInt),
      resultSet.getInt("incidents_limit"),
      resultSet.getInt("penalty_points")
    )
  }

  def add(championshipId: Int, sessionType: SessionType) : Future[SessionType] = {
    Future {
      implicit val conn = db.getConnection()
      val insertedId = insert(getColumnMapping(sessionType) ++ championshipIdMapping(championshipId))
      sessionType.copy(id = insertedId)
    }
  }

}
