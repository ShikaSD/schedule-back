package com.shika.mamk.parser.model

//It is a copy of deserialized C# class
//Don't rename the fields
//Yes, I don' like it too
//But who knows, who tells C# developers to name fields from uppercase

case class StudentCalendarEvent (
  Items: StudentDataModel
)

case class StudentDataModel (
  Data:    Seq[Seq[Int]],
  Strings: Seq[String]
)

case class StudentEvent (
  Title: String,
  EventDate: String,
  EndDate: String,
  Description: String,
  Modified: String
)