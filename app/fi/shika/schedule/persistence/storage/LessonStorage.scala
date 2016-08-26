package fi.shika.schedule.persistence.storage

import javax.inject.Inject

import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.model.Lesson
import fi.shika.schedule.persistence.profile.SlickProfile
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

trait LessonStorage {

  def all(): Future[Seq[Lesson]]
}

class LessonStorageImpl @Inject()(protected val configProvider: DatabaseConfigProvider)
  extends LessonStorage
  with TableComponent
  with SlickProfile {

  import driver.api._

  def all(): Future[Seq[Lesson]] = db.run(lessons.result)
}
