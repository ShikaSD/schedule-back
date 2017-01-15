package fi.shika.schedule.persistence.storage

import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.model.Group
import fi.shika.schedule.persistence.profile.SlickProfile

class GroupStorage extends TableComponent with SlickProfile {

  import driver.api._

  def all() = db.run(groups.sortBy(_.name).result)

  def count() = db.run(groups.length.result)

  def createAll(items: Seq[Group]) = db.run(groups returning groups ++= items)

  def deleteAll(items: Seq[Group]) = db.run(groups.filter(_.name.inSet(items.map(_.name))).delete)
}
