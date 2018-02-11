package db.queryBuilder

case class Table private(
  name: String,
  alias: String,
  columns: Seq[String],
  dependencies: Map[String, Table] = Map.empty
) {


  def getProjection() : Seq[String] = {
    columns.map(column => alias + "." + column)
  }
  def getProjectionAsString() : String = {
    getProjection().mkString(", ")
  }
  def getDependencyTables() : Seq[Table] = {
    dependencies.values.toList
  }

}

object Table {
  val emptyTable = Table("", "", Seq.empty, Map.empty)
}