package com.shika.mamk.parser.service

import org.joda.time.DateTime

trait StudentParser {
  def parseEvents (implicit startDate: DateTime): (Int, Int)
  def parseChanges(implicit startDate: DateTime): (Int, Int)
}
