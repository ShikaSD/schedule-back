package com.shika.mamk.rest.helper

import org.json4s._
import org.json4s.ext.JodaTimeSerializers
import org.json4s.native.JsonMethods._
import org.json4s.native._

object JsonHelper {

  def fromJson[T](raw: String)
                 (implicit man: Manifest[T], formats: Formats = JsonFormats ++ JodaTimeSerializers.all): T = {
    parse(raw, useBigDecimalForDouble = true).extract[T]
  }

  def fromJson[T](raw: String, serializer: Serializer[_])(implicit man: Manifest[T]): T = {
    implicit val jsonFormats = JsonFormats + serializer
    fromJson(raw)
  }

  def toJson[T](obj: T)
               (implicit formats: Formats = JsonFormats ++ JodaTimeSerializers.all): String =
    compact(render(Extraction.decompose(obj)))

  def toJson[T](obj: T, serializer: Serializer[_])(implicit man: Manifest[T]): String = {
    implicit val jsonFormats = JsonFormats + serializer
    toJson(obj)
  }

  implicit class StringOpts(val json: String) extends AnyVal {
    def parseTo[A](implicit ev: Manifest[A], fs: Formats = JsonFormats): A = JsonHelper.fromJson(json)
  }

  implicit class AnyOpts(val any: Any) extends AnyVal {
    def toJson(implicit fs: Formats = Serialization.formats(NoTypeHints)): String = JsonHelper.toJson(any)
  }
}
