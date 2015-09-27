package com.shika.mamk.rest.model.classes

import com.shika.mamk.rest.helper.JsonConverter
import com.shika.mamk.rest.model.{RestModel, RestObject}
import org.joda.time.DateTime

case class Course (
  objectId  :String            = "",
  createdAt :Option[DateTime]  = None,
  updatedAt :Option[DateTime]  = None,
  name      :String            = "",
  courseId  :String            = "",
  teacher   :String            = "",
  group     :String            = "",
  start     :Option[DateTime]  = None,
  end       :Option[DateTime]  = None
) extends RestModel {

  def get() = {
    this + Course.getAdapter.get(objectId)
  }

  def create() = {
    this + Course.getAdapter.create(this)
  }

  def update() = {
    this + Course.getAdapter.update(objectId, this)
  }

  def + (course: Course) = {
    val objectId  = if(course.objectId != "")     course.objectId   else this.objectId
    val createdAt = if(course.createdAt.nonEmpty) course.createdAt  else this.createdAt
    val updatedAt = if(course.updatedAt.nonEmpty) course.updatedAt  else this.updatedAt
    val name      = if(course.name != "")         course.name       else this.name
    val courseId  = if(course.courseId != "")     course.courseId   else this.courseId
    val teacher   = if(course.teacher != "")      course.teacher    else this.teacher
    val group     = if(course.group != "")        course.group      else this.group
    val start     = if(course.start.nonEmpty)     course.start      else this.start
    val end       = if(course.end.nonEmpty)       course.end        else this.end

    Course(objectId, createdAt, updatedAt, name, courseId, teacher, group, start, end)
  }
}

object Course extends RestObject {
  type T = Course
  protected val apiPath: String = "Course"
  protected val _converter = new JsonConverter[T]
}
