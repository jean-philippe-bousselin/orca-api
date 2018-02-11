package db.queryBuilder

case class JoinStatement private (
  hostTable: Table,
  foreignTable: Table,
  hostColumn: String = "",
  foreignColumn: String = ""
) {

  def toSqlString() : String = {
    "JOIN " + foreignTable.name + " " + foreignTable.alias +
    " ON " + hostTable.alias + "." + hostColumn + " = " + foreignTable.alias + "." + foreignColumn
  }

  def getTables() : Seq[Table] = {
    Seq(hostTable, foreignTable)
  }
}

object JoinStatement