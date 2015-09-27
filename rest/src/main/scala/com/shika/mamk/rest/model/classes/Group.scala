package com.shika.mamk.rest.model.classes

import com.shika.mamk.rest.helper.JsonConverter
import com.shika.mamk.rest.model.{RestModel, RestObject}
import org.joda.time.DateTime

case class Group (
  objectId:  String           = "",
  createdAt: Option[DateTime] = None,
  updatedAt: Option[DateTime] = None,
  name:      String           = ""
) extends RestModel {

  def get() = {
    this + Group.getAdapter.get(objectId)
  }

  def create() = {
    this + Group.getAdapter.create(this)
  }

  def update() = {
    this + Group.getAdapter.update(objectId, this)
  }
  
  def + (group: Group) = {
    val objectId  = if(group.objectId != "")     group.objectId   else this.objectId
    val createdAt = if(group.createdAt.nonEmpty) group.createdAt  else this.createdAt
    val updatedAt = if(group.updatedAt.nonEmpty) group.updatedAt  else this.updatedAt
    val name      = if(group.name != "")         group.name       else this.name

    Group(objectId, createdAt, updatedAt, name)
  }
}

object Group extends RestObject {
  type T = Group
  protected val apiPath:String = "Group"
  protected val _converter = new JsonConverter[T]
}
