package fi.shika.schedule.persistence.model

import org.joda.time.DateTime

/**
  * Representation of lesson in database
  */
case class Lesson(
  id       : Option[Long] = None,
  courseId : String,
  name     : String,
  start    : DateTime,
  end      : DateTime,
  group    : String,
  teachers  : Seq[String],
  rooms     : Seq[String]) {

  override def equals(obj: Any) = obj match {
    case l: Lesson =>
      l.courseId == this.courseId &&
      l.name     == this.name     &&
      l.start    == this.start    &&
      l.end      == this.end      &&
      l.group    == this.group    &&
      l.teachers == this.teachers &&
      l.rooms    == this.rooms
    case _ => false
  }
}
