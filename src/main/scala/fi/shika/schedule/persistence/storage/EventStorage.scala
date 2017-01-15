package fi.shika.schedule.persistence.storage

import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.model.Event
import fi.shika.schedule.persistence.profile.SlickProfile

import scala.concurrent.Future

trait EventStorage {

  def all(): Future[Seq[Event]]
}

class EventStorageImpl extends EventStorage
  with TableComponent
  with SlickProfile {

  import driver.api._

  def all() = db.run(events.result)
}
