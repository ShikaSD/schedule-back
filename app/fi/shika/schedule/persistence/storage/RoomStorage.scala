package fi.shika.schedule.persistence.storage

import javax.inject.Inject

import com.google.inject.{ImplementedBy, Singleton}
import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.model.Room
import fi.shika.schedule.persistence.profile.SlickProfile
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

@ImplementedBy(classOf[RoomStorageImpl])
trait RoomStorage {

  def all(): Future[Seq[Room]]

  def byNames(names: Seq[String]): Future[Seq[Room]]

  def create(room: Room): Future[Room]

  def delete(room: Room): Future[Int]
}

@Singleton
class RoomStorageImpl @Inject()(protected val configProvider: DatabaseConfigProvider)
  extends RoomStorage
  with TableComponent
  with SlickProfile {

  import driver.api._

  def all() = db.run(rooms.result)

  def byNames(names: Seq[String]) = db.run(rooms.filter(_.name.inSet(names)).result)

  def create(room: Room) = db.run((rooms returning rooms) += room)

  def delete(room: Room) = db.run(rooms.filter(_.id === room.id).delete)
}

