//package db.mongo
//
//import org.bson.codecs.configuration.CodecRegistry
//import org.bson.types.ObjectId
//import org.mongodb.scala.model.Filters._
//import org.mongodb.scala.{MongoClient, MongoCollection, MongoDatabase}
//import play.api.Logger
//
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future
//import scala.reflect.ClassTag
//
//trait MongoTrait {
//
//  type T
//
//  val configuration: play.api.Configuration
//  val mongoClient: MongoClient = MongoClient(configuration.underlying.getString("db.url"))
//  val database: MongoDatabase = mongoClient.getDatabase(configuration.underlying.getString("db.name"))
//
//  val collectionName: String
//
//  def getCodecRegistry() : CodecRegistry
//
//  def getCollection()(implicit ct: ClassTag[T]): MongoCollection[T] = {
//    database
//      .withCodecRegistry(getCodecRegistry())
//      .getCollection(collectionName)
//  }
//
//  def findById(id: String)(implicit ct: ClassTag[T]) : Future[Option[T]] = {
//    getCollection()
//      .find(equal("_id", new ObjectId(id)))
//      .toFuture
//      .map { results: Seq[T] =>
//        results match {
//          case x :: _ => Some(x)
//          case Nil => None
//        }
//      }
//  }
//
//  def getAll()(implicit ct: ClassTag[T]) : Future[Seq[T]] =  {
//    getCollection()
//      .find()
//      .toFuture
//      .recoverWith { case e: Throwable => Future.failed(e) }
//  }
//
//  def insert(model: T)(implicit ct: ClassTag[T]) : Future[T] = {
//    getCollection()
//      .insertOne(model)
//      .toFuture
//      .recoverWith { case e: Throwable => Future.failed(e) }
//      .map { lol => {
//        Logger.debug(lol.toString())
//        model
//      } }
//  }
//
//  def insertMany(models: Seq[T])(implicit ct: ClassTag[T]) : Future[Seq[T]] = {
//    getCollection()
//      .insertMany(models)
//      .toFuture
//      .recoverWith { case e: Throwable => Future.failed(e) }
//      .map { _ => models }
//  }
//
//}
