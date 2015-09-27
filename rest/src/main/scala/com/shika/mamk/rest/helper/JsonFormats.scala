package com.shika.mamk.rest.helper

import java.text.SimpleDateFormat

import org.json4s.DefaultFormats


object JsonFormats extends DefaultFormats {
  protected override def dateFormatter: SimpleDateFormat = {
    val f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
    f.setTimeZone(DefaultFormats.UTC)
    f
  }
}
