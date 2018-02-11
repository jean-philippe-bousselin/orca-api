package db

import java.sql.ResultSet
import javax.inject.Inject

import db.queryBuilder.Table
import models.Team
import play.api.db._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TeamDao @Inject()(
  override val db: Database
) extends DaoTrait {

  type T = Team

  override val orderByDefaultColumns = Seq("name")
  
  override val table = Table(
    "teams",
    "te",
    Seq("id", "name")
  )

  override def getColumnMapping(team: Team): Map[String, Any] = {
    Map(
      "name" -> team.name
    )
  }

  override def resultSetToModel(resultSet: ResultSet) : Team = {
    Team(
      resultSet.getInt(table.alias + ".id"),
      resultSet.getString(table.alias + ".name")
    )
  }

  def add(team: Team) : Future[Team] = {
    Future {
      val insertedId = insert(getColumnMapping(team))
      team.copy(id = insertedId)
    }
  }

  def getDefault() : Future[Team] = {
    find(Team.TEAM_PRIVATEERS_ID).map(_.get)
  }

}
