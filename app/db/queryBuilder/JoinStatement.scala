package db.queryBuilder

case class JoinStatement private (
  hostTable: String,
  foreignTable: String,
  hostColumn: String,
  foreignColumn: String
) {

  def toSqlString() : String = {
    "JOIN " + foreignTable +
    " ON " + hostTable + "." + hostColumn + " = " + foreignTable + "." + foreignColumn
  }

  def getAlias(tableName: String) : String = {
    tableName.head.toString
  }
}

object JoinStatement {

  def apply(hostTable: String, foreignTable: String, hostColumn: String ): JoinStatement =
    JoinStatement(hostTable, foreignTable, hostColumn, "id")

}