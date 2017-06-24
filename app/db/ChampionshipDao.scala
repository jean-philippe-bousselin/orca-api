package db

import java.sql.{Connection, ResultSet}
import javax.inject.Inject

import models.{Championship, ChampionshipConfiguration}
import play.api.Logger
import play.api.db._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class ChampionshipDao @Inject()(override val db: Database) extends DaoTrait {

  type T = Championship

  override val tableName = "championships"

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
}
