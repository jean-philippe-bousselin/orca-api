//package db.mongo
//
//import javax.inject.Inject
//
//import models.Track
//import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
//import org.bson.codecs.configuration.CodecRegistry
//import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
//import org.mongodb.scala.bson.codecs.Macros._
//
//class TrackDao @Inject()(override val configuration: play.api.Configuration) extends MongoTrait {
//
//  type T = Track
//
//  override val collectionName: String = "tracks"
//
//  override def getCodecRegistry() : CodecRegistry = {
//    fromRegistries(fromProviders(classOf[Track]), DEFAULT_CODEC_REGISTRY)
//  }
//
//}
