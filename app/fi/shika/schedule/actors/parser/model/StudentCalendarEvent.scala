package fi.shika.schedule.actors.parser.model

//It is a copy of deserialized C# class
//Don't rename the fields
case class StudentCalendarEvent (
  Items: StudentDataModel
)

case class StudentDataModel (
  Data: Seq[Seq[Int]],
  Strings: Seq[String]
)

case class StudentEvent (
  Title: String,
  EventDate: String,
  EndDate: String,
  Description: String,
  Modified: String
)