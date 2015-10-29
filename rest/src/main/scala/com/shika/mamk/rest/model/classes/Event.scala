package com.shika.mamk.rest.model.classes

import com.shika.mamk.rest.helper.JsonConverter
import com.shika.mamk.rest.model.{ParseDate, RestModel, RestObject}

case class Event (
  objectId    :String            = "",
  createdAt   :Option[ParseDate] = None,
  updatedAt   :Option[ParseDate] = None,
  identifier  :Long               = 0,
  name        :String            = "",
  description :String            = "",
  start       :Option[ParseDate] = None,
  end         :Option[ParseDate] = None,
  modified    :Option[ParseDate] = None,
  eventType   :String            = Event.Default
) extends RestModel {

  def get()    = this + Event.getAdapter.get(objectId)
  def create() = this + Event.getAdapter.create(this)
  def update() = this + Event.getAdapter.update(objectId, this)
  def delete() = this + Event.getAdapter.delete(objectId)

  def + (event: Event) = {
    val objectId    = if(event.objectId != "")     event.objectId     else this.objectId
    val createdAt   = if(event.createdAt.nonEmpty) event.createdAt    else this.createdAt
    val updatedAt   = if(event.updatedAt.nonEmpty) event.updatedAt    else this.updatedAt
    val id          = if(event.identifier != 0)    event.identifier   else this.identifier
    val name        = if(event.name != "")         event.name         else this.name
    val description = if(event.description != "")  event.description  else this.description
    val start       = if(event.start.nonEmpty)     event.start        else this.start
    val end         = if(event.end.nonEmpty)       event.end          else this.end
    val modified    = if(event.modified.nonEmpty)  event.modified     else this.modified
    val eventType   = if(event.eventType.nonEmpty) event.eventType    else this.eventType
    
    Event(objectId, createdAt, updatedAt, id, name, description, start, end, modified, eventType)
  }

  def equals (event: Event) = {
    (identifier == event.identifier) &&
    (name == event.name) &&
    (description == event.description) &&
    (start.get    isEqual event.start.get) &&
    (end.get      isEqual event.end.get) &&
    (modified.get isEqual event.modified.get) &&
    (eventType == eventType)
  }
}

object Event extends RestObject {
  type T = Event
  protected val apiPath: String = "Event"
  protected val _converter = new JsonConverter[T]
  
  val Default = "event"
  val Cancelled = "cancelled"
}