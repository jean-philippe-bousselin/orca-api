package db

import java.sql.ResultSet
import javax.inject.Inject

import models.Track
import play.api.Logger
import play.api.db._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class TrackDao @Inject()(override val db: Database) extends DaoTrait {

  type T = Track

  override val tableName = "tracks"
  override val orderByDefaultColumns: Seq[String] = Seq("name")

  override def getColumnMapping(track: Track): Map[String, Any] = {
    Map(
      "name" -> track.name,
      "thumbnail_url" -> track.thumbnailUrl.getOrElse("")
    )
  }

  override def resultSetToModel(resultSet: ResultSet) : Track = {
    Track(
      resultSet.getInt("id"),
      resultSet.getString("name"),
      Some(resultSet.getString("thumbnail_url"))
    )
  }

  def add(track: Track) : Future[Track] = {
    Future {
      implicit val conn = db.getConnection()
      val insertedId = insert(getColumnMapping(track))
      track.copy(id = insertedId)
    }
  }

}
