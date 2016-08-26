package fi.shika.schedule.persistence.model

import org.joda.time.DateTime

/**
  * Representation of lesson in database
  */
case class Lesson(
  id       : Option[Long],
  courseId : String,
  name     : String,
  start    : DateTime,
  end      : DateTime,
  group    : String,
  teacher  : String,
  room     : String)
