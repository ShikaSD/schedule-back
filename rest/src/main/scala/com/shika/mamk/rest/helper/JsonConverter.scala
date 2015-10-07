package com.shika.mamk.rest.helper

import java.io.OutputStream
import java.lang.reflect.Type

import com.shika.mamk.rest.model.RestModel
import retrofit.converter.Converter
import retrofit.mime.{TypedInput, TypedOutput}

import scala.io.Source

class JsonTypedOutput(bytes: Array[Byte]) extends TypedOutput {

  override def fileName(): String = null

  override def mimeType(): String = "application/json; charset=UTF-8"

  override def length(): Long = bytes.length

  override def writeTo(out: OutputStream) = out.write(bytes)
}

class JsonConverter[T <: RestModel] (implicit man: Manifest[T]) extends Converter  {

  override def toBody(o: Any): TypedOutput = {
    try {
      new JsonTypedOutput( JsonHelper.toJson(o).getBytes("utf-8") )
    }
    catch {
      case e: Exception => throw new AssertionError(e)
    }
  }

  override def fromBody(typedInput: TypedInput, jType: Type): Object = {
    //Deleting outside obj
    val json =
      Source.fromInputStream(typedInput.in).mkString
        .replaceFirst("\\{\"results\"\\:(.*)}", "$1")

    jType.toString match {
      case "scala.collection.Seq<T>" => JsonHelper.fromJson[Seq[T]](json)
      case _ => JsonHelper.fromJson[T](json)
    }
  }
}