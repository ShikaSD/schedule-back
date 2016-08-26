package fi.shika.schedule.persistence.storage

import javax.inject.Inject

import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.model.Teacher
import fi.shika.schedule.persistence.profile.SlickProfile
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

trait TeacherStorage {

  def all(): Future[Seq[Teacher]]
}

class TeacherStorageImpl @Inject()(protected val configProvider: DatabaseConfigProvider)
  extends TeacherStorage
  with TableComponent
  with SlickProfile {

  import driver.api._

  def all(): Future[Seq[Teacher]] = db.run(teachers.result)
}
