package fi.shika.schedule.persistence.storage

import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.model.Course
import fi.shika.schedule.persistence.profile.SlickProfile

class CourseStorage extends TableComponent with SlickProfile {

  import driver.api._

  def all() = db.run(courses.result)

  def byCourseId(courseId: String) = db.run(courses.filter(_.courseId === courseId).result)

  def create(course: Course) = db.run(courses returning courses += course)

  def update(course: Course) = db.run(courses.filter(_.id === course.id).update(course))

  def deleteAll(items: Seq[Course]) = db.run(courses.filter(_.id.inSet(items.map(_.id.getOrElse(-1L)))).delete)
}
