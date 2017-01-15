package fi.shika.schedule.persistence.storage

import com.google.inject.{ImplementedBy, Singleton}
import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.model.Group
import fi.shika.schedule.persistence.profile.SlickProfile

import scala.concurrent.Future

@ImplementedBy(classOf[GroupStorageImpl])
trait GroupStorage {

  /**
    * Retrieves all groups from the database sorted by name
    * @return Future with retrieved Seq[Group]
    */
  def all(): Future[Seq[Group]]

  /**
    * Counts amount of groups in database
    * @return Future with retrieved Int value
    */
  def count(): Future[Int]

  /**
    * Creates all items in the database
    * @param items to create
    * @return Future with created items
    */
  def createAll(items: Seq[Group]): Future[Seq[Group]]

  /**
    * Deletes items in the database
    * @param items to delete
    * @return Future with amount of removed items
    */
  def deleteAll(items: Seq[Group]): Future[Int]
}

@Singleton
class GroupStorageImpl extends GroupStorage
  with TableComponent
  with SlickProfile {

  import driver.api._

  def all() = db.run(groups.sortBy(_.name).result)

  def count() = db.run(groups.length.result)

  def createAll(items: Seq[Group]) = db.run(groups returning groups ++= items)

  def deleteAll(items: Seq[Group]) = db.run(groups.filter(_.name.inSet(items.map(_.name))).delete)
}
