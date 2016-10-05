package fi.shika.schedule.persistence.storage

import javax.inject.Inject

import com.google.inject.{ImplementedBy, Singleton}
import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.model.Teacher
import fi.shika.schedule.persistence.profile.SlickProfile
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

@ImplementedBy(classOf[TeacherStorageImpl])
trait TeacherStorage {

  def all(): Future[Seq[Teacher]]

  def byNames(names: Seq[String]): Future[Seq[Teacher]]

  def createAll(items: Seq[Teacher]): Future[Seq[Teacher]]
}

@Singleton
class TeacherStorageImpl @Inject()(protected val configProvider: DatabaseConfigProvider)
  extends TeacherStorage
  with TableComponent
  with SlickProfile {

  import driver.api._

  def all(): Future[Seq[Teacher]] = db.run(teachers.sortBy(_.name).result)

  def byNames(names: Seq[String]) = db.run(teachers.filter(_.name.inSet(names)).result)

  def createAll(items: Seq[Teacher]) = db.run(teachers returning teachers ++= items)
}
