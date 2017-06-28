package db

import java.sql.{Connection, ResultSet}

import db.queryBuilder._
import play.api.Logger
import play.api.db.Database

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.ClassTag

trait DaoTrait {

  type T

  val db: Database
  val table: Table
  val tableDependencies: Map[String, Table] = Map.empty
  val orderByDefaultColumns: Seq[String] = Seq.empty
  val queryBuilder = QueryBuilder()

  /**
    *
    * @param model
    * @return
    */
  def getColumnMapping(model: T) : Map[String, Any]

  def resultSetToModel(resultSet: ResultSet) : T

  /**
    * Returns last inserted ID in the current connection
    *
    * @param conn
    * @return
    */
  protected def getLastInsertedId()(implicit ct: ClassTag[T], conn: Connection): Int = {
    // @TODO refactor with query builder
    val result: ResultSet = conn.createStatement.executeQuery("SELECT LAST_INSERT_ID()")
    result.next()
    result.getInt("LAST_INSERT_ID()")
  }

  /**
    *
    * @param data
    * @param conn
    */
  def insert(data: Map[String, Any])(implicit ct: ClassTag[T], conn: Connection) = {
    try {

      queryBuilder
        .insert()
        .into(table)
        .values(data)
        .toPreparedStatement()
        .execute()

      getLastInsertedId

    } finally {
      conn.close()
    }

  }

  def find(id: Int)(implicit ct: ClassTag[T]) : Future[Option[T]] = {
    find(Predicate("id", id, SqlComparators.EQUALS, table))
  }

  def find(whereClause: Predicate)(implicit ct: ClassTag[T]) : Future[Option[T]] = {
    getWhere(Seq(whereClause)).map {
      case head :: list => Some(head)
      case _ => None
    }
  }

  /**
    *
    * @param ct
    * @return
    */
  def getAll()(implicit ct: ClassTag[T]) : Future[Seq[T]] = {
    getWhere(Seq.empty)
  }

  def getWhere(where: Predicate)(implicit ct: ClassTag[T]) : Future[Seq[T]] = {
    getWhere(Seq(where))
  }

  def getWhere(where: Seq[Predicate])(implicit ct: ClassTag[T]) : Future[Seq[T]] = Future {
    where match {
      case Nil => executeBuilder(getSelectBaseBuilder())
      case head :: Nil => executeBuilder(getSelectBaseBuilder().where(head))
      case head :: list =>
        val builder = getSelectBaseBuilder().where(head)
        val foldedBuilder = list.foldLeft(builder)(
          (builder, clause) => {
            builder.and(clause)
          }
        )
        executeBuilder(foldedBuilder)
    }
  }

  // @TODO refactor this, executeBuilder should be more generic
  protected def executeBuilder(builder: QueryBuilder = getSelectBaseBuilder()) : Seq[T] = {
    implicit val conn: Connection = db.getConnection()
    val out = new ListBuffer[T]()
    try {
      val rs = finalizeBuilder(builder)
        .toPreparedStatement()
        .executeQuery()
      while (rs.next()) {
        out += resultSetToModel(rs)
      }
    } finally {
      conn.close()
    }
    out.toList
  }

  protected def processResults(results: ResultSet) : Seq[T] = {
    val out = new ListBuffer[T]()
    while (results.next()) {
      out += resultSetToModel(results)
    }
    out.toList
  }

  protected def finalizeBuilder(builder: QueryBuilder) : QueryBuilder = {
    addOrderBy(addJoins(builder))
  }

  protected def addOrderBy(builder: QueryBuilder) : QueryBuilder = {
    orderByDefaultColumns.foldLeft(builder)(
      (builder, column) => {
        builder.orderBy(column)
      }
    )
  }
  protected def addJoins(builder: QueryBuilder) : QueryBuilder = {
    tableDependencies.foldLeft(builder)(
      (builder, dependency) => {
        builder.join(JoinStatement(table, dependency._2, dependency._1, "id"))
      }
    )
  }

  protected def getSelectBaseBuilder() : QueryBuilder = {
    queryBuilder
      .select()
      .from(table)
  }

  protected def oneOrNone(resultList : Seq[T]) = {
    resultList.map {
      case head::list => Some(head)
      case _ => None
    }
  }

  def delete(whereClause: Predicate) : Unit = {
    implicit val conn: Connection = db.getConnection()
    try {
      queryBuilder
        .delete()
        .from(table)
        .where(whereClause)
        .toPreparedStatement()
        .execute()
    } finally {
      conn.close()
    }
  }

  def update(mapping: Map[String, Any], whereClause: Predicate) : Any = {
    implicit val conn: Connection = db.getConnection()
    try {
      queryBuilder
        .update(table)
        .set(mapping)
        .where(whereClause)
        .toPreparedStatement()
        .execute()
    } catch {
      case e: Exception => Logger.error(e.getMessage)
    } finally {
      conn.close()
    }
  }

}

trait ChampionshipIdMapping {
  def championshipIdMapping(championshipId: Int) : Map[String, Any] = {
    Map("championship_id" -> championshipId)
  }
}