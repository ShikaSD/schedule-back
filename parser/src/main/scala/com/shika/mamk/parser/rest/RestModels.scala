package com.shika.mamk.parser.rest

import org.joda.time.DateTime

sealed trait RestModel {
  def objectId: Option[String]
}

case class Group(
  objectId: Option[String],
  name: String
) extends RestModel

case class Teacher(
  objectId: String,
  name: String
) extends RestModel

case class Room(
  objectId: Option[String],
  name: String
) extends RestModel

case class Course(
  objectId: Option[String],
  name: String,
  courseId: String,
  group: String,
  teacher: String,
  start: DateTime,
  end: DateTime
) extends RestModel

case class Lesson(
  objectId: Option[String],
  name: String,
  courseId: String,
  group: String,
  teacher: String,
  room: String,
  start: DateTime,
  end: DateTime
) extends RestModel

object ApiPath {
  val Group = "Group"
  val Teacher = "Teacher"
  val Room = "Room"
  val Course = "Course"
  val Lesson = "Lesson"
}