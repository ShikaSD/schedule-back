package fi.shika.schedule.persistence.storage

import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.model.Room
import fi.shika.schedule.persistence.profile.SlickProfile

class RoomStorage extends TableComponent with SlickProfile {

  import driver.api._

  def all() = db.run(rooms.sortBy(_.name).result)

  def byNames(names: Seq[String]) = db.run(rooms.filter(_.name.inSet(names)).result)

  def create(room: Room) = db.run((rooms returning rooms) += room)

  def createAll(items: Seq[Room]) = db.run(rooms returning rooms ++= items)

  def delete(room: Room) = db.run(rooms.filter(_.id === room.id).delete)

  def deleteAll(items: Seq[Room]) = db.run(rooms.filter(_.id.inSet(items.map(_.id.getOrElse(-1L)))).delete)
}

