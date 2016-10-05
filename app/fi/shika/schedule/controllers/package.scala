package fi.shika.schedule

import fi.shika.schedule.persistence.model.{Group, Teacher}
import play.api.libs.json.Json


package object controllers {
  implicit val groupWrites = Json.writes[Group]
  implicit val groupFormat = Json.format[Group]

  implicit val teacherWrites = Json.writes[Teacher]
  implicit val teacherFormat = Json.format[Teacher]
}
