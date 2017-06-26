package db.queryBuilder

import java.sql.{Connection, PreparedStatement}

import play.api.Logger

case class QueryBuilder private (
  queryType: String,
  projection: Option[Seq[String]],
  from: String,
  joins: Option[Seq[JoinStatement]],
  where: Option[SqlClause],
  and: Option[Seq[SqlClause]],
  orderBy: Option[Seq[String]],
  limit: Option[Int],
  values: Map[String, Any]
) {

  def select() : QueryBuilder = {
    copy(queryType = QueryTypes.SELECT)
  }

  def insert() : QueryBuilder = {
    copy(queryType = QueryTypes.INSERT)
  }

  def from(tableName: String) : QueryBuilder = {
    copy(from = tableName)
  }

  def into(tableName: String) : QueryBuilder = {
    copy(from = tableName)
  }

  def join(statement: JoinStatement) : QueryBuilder = {
    joins match {
      case Some(j) => copy(joins = Some(j ++ Seq(statement)))
      case None => copy(joins = Some(Seq(statement)))
    }
  }

  def project(columns: Seq[String]) : QueryBuilder = {
    queryType match {
      case QueryTypes.SELECT => copy(projection = Some(columns))
      case _ => throw new Exception("Cannot project on non select queries.")
    }
  }

  def where(clause: SqlClause) : QueryBuilder = {
    copy(where = Some(clause))
  }

  def and(clause: SqlClause) : QueryBuilder = {
    and match {
      case Some(a) => copy(and = Some(a ++ Seq(clause)))
      case None => copy(and = Some(Seq(clause)))
    }
  }

  def orderBy(column: String) : QueryBuilder = {
    orderBy match {
      case Some(o) => copy(orderBy = Some(o ++ Seq(column)))
      case None => copy(orderBy = Some(Seq(column)))
    }
  }

  def limit(limit: Int) : QueryBuilder = {
    copy(limit = Some(limit))
  }

  def values(values: Map[String, Any]) : QueryBuilder = {
    copy(values = values)
  }

  def toPreparedStatement()(implicit conn: Connection) : PreparedStatement = {
    val query = toSqlString()
    val prepStmt: PreparedStatement = conn.prepareStatement(query)

    getQueryParams().values.zipWithIndex.foreach {
      case(column, i) => prepareParameter(column, i + 1, prepStmt)
    }
    prepStmt
  }

  private def toSqlString() : String = {
    val queryString: String = queryType match {
      case QueryTypes.SELECT => buildSelectQueryString()
      case QueryTypes.INSERT => buildInsertQueryString()
      case _ => throw new Exception("Cannot build query for unknown query type.")
    }
    Logger.debug(queryString)
    queryString
  }

  private def getQueryParams() : Map[String, Any] = {
    queryType match {
      case QueryTypes.INSERT => values
      case _ => getWhereAsMap() ++ getAndAsMap()
    }
  }

  private def getWhereAsMap() : Map[String, Any] = {
    where match {
      case Some(w) => Map(w.leftOperand -> w.rightOperand)
      case None => Map.empty
    }
  }

  private def getAndAsMap() : Map[String, Any] = {
    and match {
      case Some(a) => {
        a.foldLeft(Map[String, Any]())(
          (acc, and) => {
            acc ++ Map(and.leftOperand -> and.rightOperand)
          }
        )
      }
      case None => Map.empty
    }
  }

  private def prepareParameter(column: Any, index: Int, preparedStatement: PreparedStatement) : Unit = {
    column.getClass.getTypeName match {
      case "java.lang.String" => preparedStatement.setString(index, column.toString)
      case "java.lang.Integer" => preparedStatement.setInt(index, column.asInstanceOf[Int])
      case other => throw new RuntimeException(s"Illegal type $other")
    }
  }

  private def getProjectionString(): String = {
    projection match {
      case None => "*"
      case Some(p) => p.mkString(",")
    }
  }

  // join._1 = sessions join._2 = tracks
  private def getJoinsString() : String = {
    joins match {
      case Some(j) => buildJoinsString()
      case None => ""
    }
  }

  private def buildJoinsString() : String = {
    joins.get.foldLeft("")(
      (statementsString, joinStatement ) => {
        statementsString + " " + joinStatement.toSqlString()
      }
    )
  }

  private def getWhereString() : String  = {
    where match {
      case Some(w) => "WHERE " + w.toSqlString()
      case None => ""
    }
  }

  private def getAndString() : String = {
    and match {
      case Some(a) => buildAndString()
      case None => ""
    }
  }

  private def buildAndString() : String = {
    and.get.foldLeft("")(
      (andString, clause) => {
        andString + " AND " + clause.toSqlString()
      }
    )
  }

  private def getOrderByString() : String = {
    orderBy match {
      case Some(o) => "ORDER BY " + o.mkString(",")
      case None => ""
    }
  }

  private def getLimitString(): String = {
    limit match {
      case Some(l) => "LIMIT " + limit
      case None => ""
    }
  }

  private def getColumnsAsString() : String = {
    values.keys.mkString(",")
  }

  private def getValuesString() : String = {
    values.map(_ => "?").mkString(",")
  }
  private def getFromString() : String = {
    "FROM " + from
  }
  private def buildSelectQueryString() : String = {
    buildSelectQuery().filter(_ != "").mkString(" ")
  }
  private def buildSelectQuery() : Seq[String] = {
    Seq(
      QueryTypes.SELECT,
      getProjectionString(),
      getFromString(),
      getJoinsString(),
      getWhereString(),
      getAndString(),
      getOrderByString(),
      getLimitString()
    )
  }
  private def buildInsertQueryString() : String = {
    buildInsertQuery().filter(_ != "").mkString(" ")
  }
  private def buildInsertQuery() : Seq[String] = {
    if(values.isEmpty) {
      throw new Exception("Can't perform insert without data.")
    }
    Seq(
      QueryTypes.INSERT + " INTO",
      from,
      "(" + getColumnsAsString() + ")",
      "VALUES (" + getValuesString() + ")"
    )
  }

}

object QueryBuilder {
  def apply() : QueryBuilder = {
    QueryBuilder(
      QueryTypes.SELECT, None, "", None, None, None, None, None, Map.empty
    )
  }
}

object QueryTypes {
  val SELECT = "SELECT"
  val INSERT = "INSERT"
  val UPDATE = "UPDATE"
  val DELETE = "DELETE"
  val types = Seq(SELECT, INSERT, UPDATE, DELETE)
}