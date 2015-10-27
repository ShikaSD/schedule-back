package com.shika.mamk.parser.service

import com.shika.mamk.rest.model.classes.{Group, Room}
import org.joda.time.DateTime

trait ScheduleParser {
  def parseRooms: Seq[Room]
  def parseGroups: Seq[Group]
  def parseLessons(group: Group)(implicit startDate: DateTime): (Long, Long)
}
