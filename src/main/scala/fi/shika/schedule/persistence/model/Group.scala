package fi.shika.schedule.persistence.model

/**
  * Representation of group in database
  */
case class Group(id: Option[Long] = None, name: String) {

  def sameAs(obj: Any) = obj match {
    case g: Group => g.name == this.name
    case _ => false
  }
}