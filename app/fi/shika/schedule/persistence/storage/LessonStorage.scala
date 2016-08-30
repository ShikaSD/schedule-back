package fi.shika.schedule.persistence.storage

import javax.inject.Inject

import com.github.tototoshi.slick.PostgresJodaSupport._
import com.google.inject.{ImplementedBy, Singleton}
import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.model.Lesson
import fi.shika.schedule.persistence.profile.SlickProfile
import org.joda.time.DateTime
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

@ImplementedBy(classOf[LessonStorageImpl])
trait LessonStorage {

  def all(): Future[Seq[Lesson]]

  def groupLessonsBetween(groupName: String, start: DateTime, end: DateTime): Future[Seq[Lesson]]

  def create(lesson: Lesson): Future[Lesson]

  def createAll(items: Seq[Lesson]): Future[Seq[Lesson]]

  def delete(lesson: Lesson): Future[Int]

  def deleteAll(items: Seq[Lesson]): Future[Int]
}

@Singleton
class LessonStorageImpl @Inject()(protected val configProvider: DatabaseConfigProvider)
  extends LessonStorage
  with TableComponent
  with SlickProfile {

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
