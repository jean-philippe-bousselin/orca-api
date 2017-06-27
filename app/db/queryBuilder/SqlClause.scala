package db.queryBuilder

case class SqlClause (leftOperand: String, rightOperand: Any, comparator: String, table: String = "") {

  def toSqlString() : String = {
    comparator match {
      case SqlComparators.IN => inComparison()
      case _ => regularComparisonString()
    }

  }

  private def inComparison() : String = {
    // @TODO handle subrequests
    prependPrefix()  + leftOperand.toString + " IN (" +
      rightOperand.asInstanceOf[Seq[Any]].mkString(",") +
      ")"
  }

  private def regularComparisonString() : String = {
    prependPrefix() + leftOperand.toString + " " + comparator + " ?"
  }

  private def prependPrefix() : String = {
    if(table.nonEmpty){
      table + "."
    } else {
      ""
    }
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
  val IN = "IN"
}