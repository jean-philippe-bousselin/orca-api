package db

import java.sql.ResultSet
import javax.inject.Inject

import db.queryBuilder.{Predicate, SqlComparators, Table}
import models.{Driver, Team}
import play.api.Logger
import play.api.db._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class DriverDao @Inject()(override val db: Database) extends DaoTrait {

  type T = Driver

  override val table = Table(
    "drivers",
    "d",
    Seq("id", "name")
  )

  override val orderByDefaultColumns = Seq("name")

  override def getColumnMapping(driver: Driver): Map[String, Any] = {
    Map("name" -> driver.name)
  }

  override def resultSetToModel(resultSet: ResultSet) : Driver = {
    Driver(
      resultSet.getInt(table.alias + ".id"),
      resultSet.getString(table.alias + ".name")
    )
  }

  def createIfNotExists(name: String) : Future[Driver] = {
    find(Predicate("name", name, SqlComparators.EQUALS, table)).map {
      case Some(d) => d
      case None =>
        val id = insert(getColumnMapping(Driver(0, name)))
        Driver(id, name)
    }
  }

  def updateDriver(driver: Driver) : Future[Driver] = Future {
    val whereClause = Predicate("id", driver.id, SqlComparators.EQUALS, table)
    update(getColumnMapping(driver), whereClause)
    driver
  }

}
