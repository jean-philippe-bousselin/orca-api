package db.queryBuilder

case class Table private(
  name: String,
  alias: String,
  columns: Seq[String]
) {


  def getProjection() : Seq[String] = {
    columns.map(column => alias + "." + column)
  }
  def getProjectionAsString() : String = {
    getProjection().mkString(", ")
  }

}

object Table {
  val emptyTable = Table("", "", Seq.empty)
}