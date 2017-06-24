package db.queryBuilder

case class SqlClause (leftOperand: String, rightOperand: Any, comparator: String, table: String) {

  def toSqlString() : String = {
    table + "." + leftOperand.toString + " " + comparator + " ?"
  }

}

object SqlClause {}

object SqlComparators {
  val GREATER_THAN = ">"
  val GREATER_OR_EQUALS = "=>"
  val LOWER_THAN = "<"
  val LOWER_OR_EQUALS = "=<"
  val EQUALS = "="
  val NOT_EQUALS = "<>"
}