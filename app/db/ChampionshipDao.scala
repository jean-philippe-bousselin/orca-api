package db

import java.sql.{Connection, ResultSet}
import javax.inject.Inject

import db.queryBuilder.{SqlClause, SqlComparators}
import models.{Championship, ChampionshipConfiguration, SessionType}
import play.api.Logger
import play.api.db._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChampionshipDao @Inject()(
  override val db: Database,
  sessionTypeDao: SessionTypeDao
) extends DaoTrait {

  type T = Championship

  override val tableName = "championships"
  val foreignKey = "championship_id"

  override def getColumnMapping(champ: Championship): Map[String, Any] = {
    Map(
      "name" -> champ.name,
      "description" -> champ.description.getOrElse("")
    )
  }

  override def resultSetToModel(resultSet: ResultSet) : Championship = {
    Championship(
      resultSet.getInt("id"),
      resultSet.getString("name"),
      Some(resultSet.getString("description"))
    )
  }

  def add(championship: Championship) : Future[Championship] = {
    Future {
      implicit val conn = db.getConnection()
      val insertedId = insert(getColumnMapping(championship))
      championship.copy(id = insertedId)
    }
  }

  def saveConfiguration(id: Int, configuration: ChampionshipConfiguration) : Future[Boolean] = {
    Future {
      configuration.sessionTypes.map { sessionType =>
        sessionTypeDao.add(id, sessionType)
      }
      true
    }
  }

  def getConfiguration(id: Int) : Future[ChampionshipConfiguration] = {
    val clause = SqlClause(foreignKey, id, SqlComparators.EQUALS, sessionTypeDao.tableName)
    sessionTypeDao.getWhere(clause).map { sessionTypes =>
      ChampionshipConfiguration(sessionTypes, None)
    }
  }

}
