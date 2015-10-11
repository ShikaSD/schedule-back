package com.shika.mamk.rest.model

import org.joda.time.DateTime

case class ParseDate(iso: DateTime, __type :String =  "Date") {

  def isEqual(date: ParseDate) = {
    iso isEqual date.iso
  }

  def isAfter(date: ParseDate) = {
    iso isAfter date.iso
  }

  def isBefore(date: ParseDate) = {
    iso isBefore date.iso
  }
}

object ParseDate {
  def apply (date: DateTime) = {
    new ParseDate(date.toDateTimeISO)
  }
}