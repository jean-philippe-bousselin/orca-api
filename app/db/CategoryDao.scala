package db

import java.sql.ResultSet
import javax.inject.Inject

import db.queryBuilder.Table
import models.Category
import play.api.db.Database

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class CategoryDao @Inject()(override val db: Database) extends DaoTrait {

  override type T = Category
  override val table: Table = Table(
    "categories",
    "ca",
    Seq("id", "name")
  )

  override def getColumnMapping(category: Category): Map[String, Any] = {
    Map("name" -> category.name)
  }

  override def resultSetToModel(resultSet: ResultSet): Category = {
    Category(
      resultSet.getInt(table.alias + ".id"),
      resultSet.getString(table.alias + ".name")
    )
  }

  def getDefault() : Future[Category] = {
    find(Category.OVERALL_CATEGORY_ID).map(_.get)
  }
}