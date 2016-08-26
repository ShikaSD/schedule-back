package fi.shika.schedule.persistence.model

import org.joda.time.DateTime

/**
  * Representation of course in database
  */
case class Course(
  id       : Option[Long],
  courseId : String,
  name     : String,
  teacher  : String,
  group    : String,
  start    : DateTime,
  end      : DateTime,
  parent   : Boolean = false)
