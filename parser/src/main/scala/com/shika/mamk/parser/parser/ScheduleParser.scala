package com.shika.mamk.parser.parser

import com.shika.mamk.rest.model.classes.{Room, Group}

trait ScheduleParser {
  def parseRooms: Seq[Room]
  def parseGroups: Seq[Group]
  def parseLessons(group: Group): (Long, Long)
}
