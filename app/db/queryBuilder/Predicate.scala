package db.queryBuilder

case class Predicate(leftOperand: String, rightOperand: Any, comparator: String, table: Table) {

  def toSqlString() : String = {
    comparator match {
      case SqlComparators.IN => inComparison()
      case _ => regularComparisonString()
    }
  }

  private def inComparison() : String = {
    // @TODO handle subrequests
    prependAlias()  + leftOperand.toString + " IN (" +
      rightOperand.asInstanceOf[Seq[Any]].mkString(",") +
      ")"
  }

  private def regularComparisonString() : String = {
    prependAlias() + leftOperand.toString + " " + comparator + " ?"
  }

  private def prependAlias() : String = {
    table.alias + "."
  }


}

object Predicate {}

object SqlComparators {
  val GREATER_THAN = ">"
  val GREATER_OR_EQUALS = "=>"
  val LOWER_THAN = "<"
  val LOWER_OR_EQUALS = "=<"
  val EQUALS = "="
  val NOT_EQUALS = "<>"
  val IN = "IN"
}