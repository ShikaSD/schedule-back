package fi.shika.schedule.persistence.storage

import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.model.Teacher
import fi.shika.schedule.persistence.profile.SlickProfile

import scala.concurrent.Future

class TeacherStorage extends TableComponent with SlickProfile {

  import driver.api._

  def all(): Future[Seq[Teacher]] = db.run(teachers.sortBy(_.name).result)

  def byNames(names: Seq[String]) = db.run(teachers.filter(_.name.inSet(names)).result)

  def createAll(items: Seq[Teacher]) = db.run(teachers returning teachers ++= items)
}
