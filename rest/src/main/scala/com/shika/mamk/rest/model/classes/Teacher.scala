package com.shika.mamk.rest.model.classes

import com.shika.mamk.rest.helper.JsonConverter
import com.shika.mamk.rest.model.{RestModel, RestObject}
import org.joda.time.DateTime

case class Teacher (
  objectId: String            = "",
  createdAt: Option[DateTime] = None,
  updatedAt: Option[DateTime] = None,
  name: String                = ""
) extends RestModel {

  def get()    = this + Teacher.getAdapter.get(objectId)
  def create() = this + Teacher.getAdapter.create(this)
  def update() = this + Teacher.getAdapter.update(objectId, this)
  def delete() = this + Teacher.getAdapter.delete(objectId)

  def + (teacher: Teacher) = {
    val objectId  = if(teacher.objectId != "")     teacher.objectId   else this.objectId
    val createdAt = if(teacher.createdAt.nonEmpty) teacher.createdAt  else this.createdAt
    val updatedAt = if(teacher.updatedAt.nonEmpty) teacher.updatedAt  else this.updatedAt
    val name      = if(teacher.name != "")         teacher.name       else this.name

    Teacher(objectId, createdAt, updatedAt, name)
  }

  def equals (teacher: Teacher) = this.name == teacher.name
}

object Teacher extends RestObject {
  type T = Teacher
  protected val apiPath: String = "Teacher"
  protected val _converter = new JsonConverter[T]
}
