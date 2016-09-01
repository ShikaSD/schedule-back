package fi.shika.schedule

import fi.shika.schedule.persistence.model.Group
import play.api.libs.json.Json


package object controllers {
  implicit val groupWrites = Json.writes[Group]
  implicit val groupFormat = Json.format[Group]
}
