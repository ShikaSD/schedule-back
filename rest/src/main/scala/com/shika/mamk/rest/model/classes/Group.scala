package com.shika.mamk.rest.model.classes

import com.shika.mamk.rest.helper.JsonConverter
import com.shika.mamk.rest.model.{BaseModel, BaseObject, ParseDate}

case class Group (
  objectId:  String           = "",
  createdAt: Option[ParseDate] = None,
  updatedAt: Option[ParseDate] = None,
  name:      String           = ""
) extends BaseModel {

  def get    = this + Group.getAdapter.get(objectId)
  def create = this + Group.getAdapter.create(this)
  def update = this + Group.getAdapter.update(objectId, this)
  def delete = this + Group.getAdapter.delete(objectId)
  
  def + (group: Group) = {
    val objectId  = if(group.objectId != "")     group.objectId   else this.objectId
    val createdAt = if(group.createdAt.nonEmpty) group.createdAt  else this.createdAt
    val updatedAt = if(group.updatedAt.nonEmpty) group.updatedAt  else this.updatedAt
    val name      = if(group.name != "")         group.name       else this.name

    Group(objectId, createdAt, updatedAt, name)
  }

  def equals (group: Group) = this.name == group.name
}

object Group extends BaseObject {
  type T = Group
  protected val apiPath:String = "Group"
  protected val _converter = new JsonConverter[T]
}
