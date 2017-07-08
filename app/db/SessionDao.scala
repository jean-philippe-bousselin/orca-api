package db

import java.sql.{Connection, ResultSet}
import javax.inject.Inject

import db.queryBuilder.{JoinStatement, Predicate, SqlComparators, Table}
import models.Session
import play.api.Logger
import play.api.db._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class SessionDao @Inject()(
  override val db: Database,
  trackDao: TrackDao,
  sessionTypeDao: SessionTypeDao
) extends DaoTrait with ChampionshipIdMapping {

  type T = Session

  override val table = Table(
    "sessions",
    "s",
    Seq("id", "name", "date", "time", "track_id", "session_type_id", "championship_id"),
    Map(
      "track_id" -> trackDao.table,
      "session_type_id" -> sessionTypeDao.table
    )
  )

  override val orderByDefaultColumns: Seq[String] = Seq("date")

  override def getColumnMapping(session: Session): Map[String, Any] = {
    Map(
      "name" -> session.name,
      "date" -> session.date,
      "time" -> session.time,
      "track_id" -> session.track.id,
      "session_type_id" -> session.sessionType.id
    )
  }

  override def resultSetToModel(resultSet: ResultSet) : Session = {

    Session(
      resultSet.getInt(table.alias + ".id"),
      resultSet.getString(table.alias + ".name"),
      resultSet.getString(table.alias + ".date"),
      resultSet.getString(table.alias + ".time"),
      trackDao.resultSetToModel(resultSet),
      sessionTypeDao.resultSetToModel(resultSet)
    )
  }

  def add(championshipId: Int, session: Session) : Future[Session] = {
    Future {
      val insertedId = insert(getColumnMapping(session) ++ championshipIdMapping(championshipId))
      session.copy(id = insertedId)
    }
  }

  def getAll(championshipId: Int) : Future[Seq[Session]] = {
    getWhere(Predicate("championship_id", championshipId, SqlComparators.EQUALS, table))
  }

  def getChampionshipId(sessionId: Int) : Future[Int] = {
    Future {
      implicit val conn: Connection = db.getConnection()
      try {
        val rs =
          queryBuilder
            .select(Seq("championship_id"))
            .from(table)
            .where(Predicate("id", sessionId, SqlComparators.EQUALS, table))
            .toPreparedStatement()
            .executeQuery()

        rs.next()
        rs.getInt("championship_id")

      } finally {
        conn.close()
      }
    }
  }

}
