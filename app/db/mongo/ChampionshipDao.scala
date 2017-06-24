//package db.mongo
//
//import javax.inject.Inject
//
//import models.{Championship, ChampionshipConfiguration, RaceType}
//import org.bson.codecs.configuration.CodecRegistries.{fromProviders, fromRegistries}
//import org.bson.codecs.configuration.CodecRegistry
//import org.mongodb.scala.bson.codecs.DEFAULT_CODEC_REGISTRY
//import org.mongodb.scala.bson.codecs.Macros._
//
//class ChampionshipDao @Inject()(override val configuration: play.api.Configuration) extends MongoTrait {
//
//  type T = Championship
//
//  override val collectionName: String = "championships"
//
//  override def getCodecRegistry() : CodecRegistry = {
//    // Warning: order of providers is important !
//    fromRegistries(
//      DEFAULT_CODEC_REGISTRY,
//      fromProviders(classOf[RaceType]),
//      fromProviders(classOf[ChampionshipConfiguration]),
//      fromProviders(classOf[Championship])
//    )
//  }
//}
