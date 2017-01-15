package fi.shika.schedule.persistence.model

import org.joda.time.DateTime

/**
  * Representation of course in database
  */
case class Course(
  id       : Option[Long] = None,
  courseId : String,
  name     : String,
  teachers : Seq[String] = Nil,
  group    : String,
  start    : DateTime,
  end      : DateTime,
  parent   : Boolean = false) {

  def sameAs(obj: Any) = obj match {
    case c: Course =>
      this.courseId == c.courseId &&
      this.name     == c.name     &&
      this.teachers  == c.teachers  &&
      this.group    == c.group    &&
      this.parent   == c.parent

    case _ => false
  }
}
