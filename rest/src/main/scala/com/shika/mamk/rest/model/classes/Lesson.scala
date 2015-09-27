package com.shika.mamk.rest.model.classes

import com.shika.mamk.rest.helper.JsonConverter
import com.shika.mamk.rest.model.{RestModel, RestObject}
import org.joda.time.DateTime

case class Lesson (
  objectId  :String            = "",
  createdAt :Option[DateTime]  = None,
  updatedAt :Option[DateTime]  = None,
  name      :String            = "",
  courseId  :String            = "",
  teacher   :String            = "",
  group     :String            = "",
  room      :String            = "",
  start     :Option[DateTime]  = None,
  end       :Option[DateTime]  = None
) extends RestModel {

  def get() = {
    this + Lesson.getAdapter.get(objectId)
  }

  def create() = {
    this + Lesson.getAdapter.create(this)
  }

  def update() = {
    this + Lesson.getAdapter.update(objectId, this)
  }

  def + (lesson: Lesson) = {
    val objectId  = if(lesson.objectId != "")     lesson.objectId   else this.objectId
    val createdAt = if(lesson.createdAt.nonEmpty) lesson.createdAt  else this.createdAt
    val updatedAt = if(lesson.updatedAt.nonEmpty) lesson.updatedAt  else this.updatedAt
    val name      = if(lesson.name != "")         lesson.name       else this.name
    val lessonId  = if(lesson.courseId != "")     lesson.courseId   else this.courseId
    val teacher   = if(lesson.teacher != "")      lesson.teacher    else this.teacher
    val group     = if(lesson.group != "")        lesson.group      else this.group
    val room      = if(lesson.room != "")         lesson.room       else this.room
    val start     = if(lesson.start.nonEmpty)     lesson.start      else this.start
    val end       = if(lesson.end.nonEmpty)       lesson.end        else this.end

    Lesson(objectId, createdAt, updatedAt, name, lessonId, teacher, group, room, start, end)
  }
}

object Lesson extends RestObject {
  type T = Lesson
  protected val apiPath: String = "Lesson"
  protected val _converter = new JsonConverter[T]
}
