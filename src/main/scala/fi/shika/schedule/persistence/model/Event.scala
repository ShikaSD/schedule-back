package fi.shika.schedule.persistence.model

import org.joda.time.DateTime

/**
  * Event representation in database
  */
case class Event(
  id          : Option[Long],
  name        : String,
  description : String,
  start       : DateTime,
  end         : DateTime,
  modified    : DateTime,
  eventType   : String = "")