//package db.mongo
//
//import javax.inject.Inject
//
//import models.Result
//import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
//import org.bson.codecs.configuration.CodecRegistry
//import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
//import org.mongodb.scala.bson.codecs.Macros._
//import org.mongodb.scala.model.Filters.{equal, in}
//
//import scala.concurrent.Future
//
//class ResultDao @Inject()(override val configuration: play.api.Configuration) extends MongoTrait {
//
//  type T = Result
//
//  override val collectionName: String = "results"
//
//  override def getCodecRegistry() : CodecRegistry = {
//    fromRegistries(fromProviders(classOf[Result]), DEFAULT_CODEC_REGISTRY)
//  }
//
//  def getForSession(sessionId: String) : Future[Seq[Result]]= {
//    getCollection()
//      .find(equal("sessionId", sessionId))
//      .toFuture
//  }
//
//  def getForSessions(ids: Seq[String]) : Future[Seq[Result]] = {
//    getCollection()
//      .find(in("sessionId", getCollection))
//      .toFuture
//  }
//
//}
