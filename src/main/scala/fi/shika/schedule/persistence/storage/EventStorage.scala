package fi.shika.schedule.persistence.storage

import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.profile.SlickProfile

class EventStorage extends TableComponent with SlickProfile {

  import driver.api._

  def all() = db.run(events.result)
}
