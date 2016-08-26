package fi.shika.schedule.persistence.storage

import javax.inject.Inject

import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.model.Course
import fi.shika.schedule.persistence.profile.SlickProfile
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

trait CourseStorage {

  def all(): Future[Seq[Course]]
}

class CourseStorageImpl @Inject()(protected val configProvider: DatabaseConfigProvider)
  extends CourseStorage
  with TableComponent
  with SlickProfile {

  import driver.api._

  def all() = db.run(courses.result)
}
