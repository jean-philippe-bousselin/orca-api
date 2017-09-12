package db

import java.sql.ResultSet
import javax.inject.Inject

import db.queryBuilder.{Predicate, SqlComparators, Table}
import models.{Driver, Team}
import play.api.Logger
import play.api.db._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DriverDao @Inject()(override val db: Database, teamDao: TeamDao) extends DaoTrait {

  type T = Driver

  override val table = Table(
    "drivers",
    "d",
    Seq("id", "name", "category","team_id"),
    Map("team_id" -> teamDao.table)
  )

  override val orderByDefaultColumns = Seq("name")

  override def getColumnMapping(driver: Driver): Map[String, Any] = {
    Logger.error(driver.toString)
    Map(
      "name" -> driver.name,
      "category" -> driver.category,
      "team_id" -> driver.team.map(_.id).getOrElse(Team.TEAM_PRIVATEERS_ID) // defaults privateers team
    )
  }

  override def resultSetToModel(resultSet: ResultSet) : Driver = {
    Driver(
      resultSet.getInt(table.alias + ".id"),
      resultSet.getString(table.alias + ".name"),
      resultSet.getString(table.alias + ".category"),
      Some(teamDao.resultSetToModel(resultSet))
    )
  }

  // @TODO we need to implement a link to championships somehow
  def getChampionshipDrivers(championshipId: Int) : Future[Seq[Driver]] = {
    getAll()
  }

  def createIfNotExists(name: String) : Future[Driver] = {
    find(Predicate("name", name, SqlComparators.EQUALS, table)).map {
      case Some(d) => d
      case None =>
        val id = insert(getColumnMapping(Driver(0, name, Driver.DEFAULT_CATEGORY, None)))
        Driver(id, name, Driver.DEFAULT_CATEGORY, None)
    }
  }

  def updateDriver(driver: Driver) : Future[Driver] = Future {
    val whereClause = Predicate("id", driver.id, SqlComparators.EQUALS, table)
    update(getColumnMapping(driver), whereClause)
    driver
  }

}
