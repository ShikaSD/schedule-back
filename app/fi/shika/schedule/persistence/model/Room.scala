package fi.shika.schedule.persistence.model

/**
  * Room representation in database
  */
case class Room(id: Option[Long] = None, name: String, description: String = "") {

  def sameAs(obj: Any) = obj match {
    case r: Room => r.name == this.name && r.description == this.description
    case _ => false
  }
}
