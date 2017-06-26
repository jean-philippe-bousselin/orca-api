package db

import java.sql.{Connection, PreparedStatement, ResultSet}

import db.queryBuilder.{JoinStatement, QueryBuilder, SqlClause, SqlComparators}
import play.api.Logger
import play.api.db.Database

import scala.collection.mutable.ListBuffer
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.reflect.ClassTag

trait DaoTrait {

  type T

  val db: Database
  val tableName: String
  val tableDependencies: Map[String, String] = Map.empty
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
        .into(tableName)
        .values(data)
        .toPreparedStatement()
        .execute()

      getLastInsertedId

    } finally {
      conn.close()
    }

  }

  def find(id: Int)(implicit ct: ClassTag[T]) : Future[Option[T]] = {
    find(SqlClause("id", id, SqlComparators.EQUALS, tableName))
  }

  def find(whereClause: SqlClause)(implicit ct: ClassTag[T]) : Future[Option[T]] = {
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

  def getWhere(where: SqlClause)(implicit ct: ClassTag[T]) : Future[Seq[T]] = {
    getWhere(Seq(where))
  }

  def getWhere(where: Seq[SqlClause])(implicit ct: ClassTag[T]) : Future[Seq[T]] = Future {
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
        builder.join(JoinStatement(tableName, dependency._2, dependency._1))
      }
    )
  }

  protected def getSelectBaseBuilder() : QueryBuilder = {
    queryBuilder.select().from(tableName)
  }

  protected def oneOrNone(resultList : Seq[T]) = {
    resultList.map {
      case head::list => Some(head)
      case _ => None
    }
  }

}

trait ChampionshipIdMapping {
  def championshipIdMapping(championshipId: Int) : Map[String, Any] = {
    Map("championship_id" -> championshipId)
  }
}