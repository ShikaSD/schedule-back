package fi.shika.schedule.persistence.model

import org.joda.time.DateTime

/**
  * Representation of course in database
  */
case class Course(
  id       : Option[Long] = None,
  courseId : String,
  name     : String,
  teacher  : String = "",
  group    : String,
  start    : DateTime,
  end      : DateTime,
  parent   : Boolean = false) {

  override def equals(obj: Any) = obj match {
    case c: Course =>
      this.courseId == c.courseId &&
      this.name     == c.name     &&
      this.teacher  == c.teacher  &&
      this.group    == c.group    &&
      this.start    == c.start    &&
      this.end      == c.end      &&
      this.parent   == c.parent

    case _ => false
  }
}
