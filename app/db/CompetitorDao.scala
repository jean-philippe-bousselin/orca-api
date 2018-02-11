package db

import java.sql.ResultSet
import javax.inject.Inject

import db.queryBuilder.{Predicate, SqlComparators, Table}
import models.{Competitor, Driver}
import play.api.db.Database

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


class CompetitorDao @Inject()(
  override val db: Database,
  driverDao: DriverDao,
  teamDao: TeamDao,
  championshipDao: ChampionshipDao,
  categoryDao: CategoryDao
) extends DaoTrait {

  override type T = Competitor
  override val table: Table = Table(
    "competitors",
    "co",
    Seq("driver_id", "team_id", "championship_id", "category_id"),
    Map(
      "driver_id" -> driverDao.table,
      "team_id" -> teamDao.table,
      "category_id" -> categoryDao.table
    )
  )

  override def getColumnMapping(competitor: Competitor): Map[String, Any] = {
    Map(
      "driver_id" -> competitor.driver.id,
      "team_id" -> competitor.team.id,
      "championship_id" -> competitor.championshipId,
      "category_id" -> competitor.category.id
    )
  }

  override def resultSetToModel(resultSet: ResultSet): Competitor = {
    Competitor(
      driverDao.resultSetToModel(resultSet),
      resultSet.getInt(table.alias + ".championship_id"),
      teamDao.resultSetToModel(resultSet),
      categoryDao.resultSetToModel(resultSet)
    )
  }

  def createIfNotExists(name: String, championshipId: Int) : Future[Competitor] = {
    driverDao.createIfNotExists(name).flatMap { driver =>
      find(Seq(
        Predicate("driver_id", driver.id, SqlComparators.EQUALS, table),
        Predicate("championship_id", championshipId, SqlComparators.EQUALS, table)
      )).flatMap {
        case Some(c) => Future.successful(c)
        case None =>
          for {
            defaultTeam <- teamDao.getDefault()
            defaultCategory <- categoryDao.getDefault()
          } yield {
            val newCompetior = Competitor(driver, championshipId, defaultTeam, defaultCategory)
            insert(getColumnMapping(newCompetior))
            newCompetior
          }
      }
    }
  }

  def getAllForChampionship(championshipId: Int) : Future[Seq[Competitor]] = {
    getWhere(Predicate("championship_id", championshipId, SqlComparators.EQUALS, table))
  }

  def getFromDriverId(driverId: Int, championshipId: Int) : Future[Option[Competitor]] = {
    find(Seq(
      Predicate("championship_id", championshipId, SqlComparators.EQUALS, table),
      Predicate("driver_id", driverId, SqlComparators.EQUALS, table)
    ))
  }

}
