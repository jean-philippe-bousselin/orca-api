//package db.mongo
//
//import javax.inject.Inject
//
//import models.Session
//import org.bson.codecs.UuidCodec
//import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
//import org.bson.codecs.configuration.CodecRegistry
//import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
//import org.mongodb.scala.bson.codecs.Macros._
//import org.mongodb.scala.model.Filters.equal
//import play.api.Logger
//
//import scala.concurrent.ExecutionContext.Implicits.global
//import scala.concurrent.Future
//
//
//class SessionDao @Inject()(override val configuration: play.api.Configuration) extends MongoTrait {
//
//  type T = Session
//
//  override val collectionName: String = "sessions"
//
//  override def getCodecRegistry() : CodecRegistry = {
//
//    val lol: CodecRegistry = fromProviders(classOf[Session])
//
//    fromRegistries(fromProviders(classOf[Session]), DEFAULT_CODEC_REGISTRY)
//  }
//
//  def getForChampionship(championshipId: String) : Future[Seq[Session]]= {
//
//    new UuidCodec()
//
//    getCollection()
//      .find(equal("championshipId", championshipId))
//      .toFuture
//      .map { lol => {
//        Logger.debug(lol.toString())
//        lol
//      } }
//  }
//
//}
