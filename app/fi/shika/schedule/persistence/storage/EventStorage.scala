package fi.shika.schedule.persistence.storage

import javax.inject.Inject

import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.model.Event
import fi.shika.schedule.persistence.profile.SlickProfile
import play.api.db.slick.DatabaseConfigProvider

import scala.concurrent.Future

trait EventStorage {

  def all(): Future[Seq[Event]]
}

class EventStorageImpl @Inject()(protected val configProvider: DatabaseConfigProvider)
  extends EventStorage
  with TableComponent
  with SlickProfile {

  import driver.api._

  def all() = db.run(events.result)
}
