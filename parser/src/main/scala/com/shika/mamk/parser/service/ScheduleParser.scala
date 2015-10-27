package com.shika.mamk.parser.service

import com.shika.mamk.rest.model.classes.{Group, Room}

trait ScheduleParser {
  def parseRooms: Seq[Room]
  def parseGroups: Seq[Group]
  def parseLessons(group: Group): (Long, Long)
}
