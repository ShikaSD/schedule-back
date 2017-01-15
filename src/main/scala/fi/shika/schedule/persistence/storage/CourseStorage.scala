package fi.shika.schedule.persistence.storage

import com.google.inject.{ImplementedBy, Singleton}
import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.model.Course
import fi.shika.schedule.persistence.profile.SlickProfile

import scala.concurrent.Future

@ImplementedBy(classOf[CourseStorageImpl])
trait CourseStorage {

  def all(): Future[Seq[Course]]

  def byCourseId(courseId: String): Future[Seq[Course]]

  def create(course: Course): Future[Course]

  def update(course: Course): Future[Int]

  def deleteAll(items: Seq[Course]): Future[Int]
}

@Singleton
class CourseStorageImpl extends CourseStorage
  with TableComponent
  with SlickProfile {

  import driver.api._

  def all() = db.run(courses.result)

  def byCourseId(courseId: String) = db.run(courses.filter(_.courseId === courseId).result)

  def create(course: Course) = db.run(courses returning courses += course)

  def update(course: Course) = db.run(courses.filter(_.id === course.id).update(course))

  def deleteAll(items: Seq[Course]) = db.run(courses.filter(_.id.inSet(items.map(_.id.getOrElse(-1L)))).delete)
}
