package com.shika.mamk.parser.service

trait StudentParser {
  def parseEvents: (Int, Int)
  def parseChanges: (Int, Int)
}
