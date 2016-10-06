package fi.shika.schedule

import fi.shika.schedule.persistence.model.{Group, Room, Teacher}
import play.api.libs.json.Json


package object controllers {
  implicit val groupWrites   = Json.writes[Group]
  implicit val groupFormat   = Json.format[Group]

  implicit val teacherWrites = Json.writes[Teacher]
  implicit val teacherFormat = Json.format[Teacher]

  implicit val roomWrites    = Json.writes[Room]
  implicit val roomFormat    = Json.format[Room]
}
