package db

import java.sql.ResultSet
import javax.inject.Inject

import db.queryBuilder.{SqlClause, SqlComparators}
import models.Session
import play.api.db._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SessionDao @Inject()(
  override val db: Database,
  trackDao: TrackDao
) extends DaoTrait with ChampionshipIdMapping {

  type T = Session

  override val tableName = "sessions"
  override val tableDependencies: Map[String, String] = Map("track_id" -> trackDao.tableName)
  override val orderByDefaultColumns: Seq[String] = Seq("date")

  override def getColumnMapping(session: Session): Map[String, Any] = {
    Map(
      "name" -> session.name,
      "date" -> session.date,
      "time" -> session.time,
      "track_id" -> session.track.id
    )
  }

  override def resultSetToModel(resultSet: ResultSet) : Session = {
    Session(
      resultSet.getInt("id"),
      resultSet.getString("name"),
      resultSet.getString("date"),
      resultSet.getString("time"),
      trackDao.resultSetToModel(resultSet)
    )
  }

  def add(championshipId: Int, session: Session) : Future[Session] = {
    Future {
      implicit val conn = db.getConnection()
      val insertedId = insert(getColumnMapping(session) ++ championshipIdMapping(championshipId))
      session.copy(id = insertedId)
    }
  }

  def getAll(championshipId: Int) : Future[Seq[Session]] = {
    getWhere(SqlClause("championship_id", championshipId, SqlComparators.EQUALS, tableName))
  }

}
