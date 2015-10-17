package com.shika.mamk.parser.parser

trait StudentParser {
  def parseEvents: (Int, Int)
  def parseChanges: (Int, Int)
}
