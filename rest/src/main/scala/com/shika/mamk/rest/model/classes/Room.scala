package com.shika.mamk.rest.model.classes

import com.shika.mamk.rest.helper.JsonConverter
import com.shika.mamk.rest.model._

case class Room (
  objectId:    String           = "",
  createdAt:   Option[ParseDate] = None,
  updatedAt:   Option[ParseDate] = None,
  name:        String           = "",
  description: String           = ""
) extends RestModel {

  def get()    = this + Room.getAdapter.get(objectId)
  def create() = this + Room.getAdapter.create(this)
  def update() = this + Room.getAdapter.update(objectId, this)
  def delete() = this + Room.getAdapter.delete(objectId)

  def + (room: Room) = {
    val objectId    = if(room.objectId != "")     room.objectId     else this.objectId
    val createdAt   = if(room.createdAt.nonEmpty) room.createdAt    else this.createdAt
    val updatedAt   = if(room.updatedAt.nonEmpty) room.updatedAt    else this.updatedAt
    val name        = if(room.name != "")         room.name         else this.name
    val description = if(room.description != "")  room.description  else this.description

    Room(objectId, createdAt, updatedAt, name, description)
  }

  def equals (room: Room) = {
    (this.name == room.name) && (this.description == room.description)
  }
}

object Room extends RestObject {
  type T = Room
  protected val apiPath: String = "Room"
  protected val _converter = new JsonConverter[T]
}
