package fi.shika.schedule.persistence.storage

import javax.inject.Inject

import com.google.inject.{ImplementedBy, Singleton}
import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.model.Group
import fi.shika.schedule.persistence.profile.SlickProfile
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

@ImplementedBy(classOf[GroupStorageImpl])
trait GroupStorage {

  def all(): Future[Seq[Group]]

  def create(group: Group): Future[Group]

  def createAll(items: Seq[Group]): Future[Seq[Group]]

  def delete(group: Group): Future[Int]

  def deleteAll(items: Seq[Group]): Future[Int]
}

@Singleton
class GroupStorageImpl @Inject()(protected val configProvider: DatabaseConfigProvider)
  extends GroupStorage
  with TableComponent
  with SlickProfile {

  import driver.api._

  def all() = db.run(groups.result)

  def create(group: Group) = db.run((groups returning groups) += group)

  def createAll(items: Seq[Group]) = db.run(groups returning groups ++= items)

  def delete(group: Group) = db.run(groups.filter(_.id === group.id).delete)

  def deleteAll(items: Seq[Group]) = db.run(groups.filter(_.name.inSet(items.map(_.name))).delete)
}
