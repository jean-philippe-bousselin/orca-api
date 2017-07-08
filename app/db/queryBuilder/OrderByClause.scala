package db.queryBuilder

/**
  * Created by blackcat on 7/8/17.
  */
case class OrderByClause(
  column: String,
  direction: String = "ASC",
  table: Table
) {

  def getSqlString() : String = {
    table.alias + "." + column + " " + direction
  }

}

object OrderByClause {}
