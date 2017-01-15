package fi.shika.schedule.persistence.storage

import com.github.tototoshi.slick.PostgresJodaSupport._
import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.model.Lesson
import fi.shika.schedule.persistence.profile.SlickProfile
import org.joda.time.DateTime

import scala.concurrent.Future

class LessonStorage extends TableComponent with SlickProfile {

  import driver.api._

  def all(): Future[Seq[Lesson]] = db.run(lessons.result)

  def groupLessonsBetween(groupName: String, start: DateTime, end: DateTime) = db.run(
    lessons.filter(_.group === groupName)
      .filter(_.start >= start.bind)
      .filter(_.end <= end.bind)
      .result
  )

  def create(lesson: Lesson) = db.run((lessons returning lessons) += lesson)

  def createAll(items: Seq[Lesson]) = db.run((lessons returning lessons) ++= items)

  def delete(lesson: Lesson) = db.run(lessons.filter(_.id === lesson.id).delete)

  def deleteAll(items: Seq[Lesson]) = db.run(
    lessons.filter(
      _.id.inSet(items.map(_.id.getOrElse(-1L)))
    ).delete
  )
}
