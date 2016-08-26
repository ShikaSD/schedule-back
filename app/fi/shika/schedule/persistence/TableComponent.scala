package fi.shika.schedule.persistence

import com.github.tototoshi.slick.PostgresJodaSupport._
import fi.shika.schedule.persistence.model._
import fi.shika.schedule.persistence.profile.SlickProfile
import org.joda.time.DateTime

/**
  * Created by ashikov on 10/08/16.
  */
trait TableComponent { self: SlickProfile =>

  import driver.api._

  protected abstract class BaseTable[T](tag: Tag, name: String) extends Table[T](tag, "sch_" + name) {
    def id = column[Option[Long]]("id", O.PrimaryKey, O.AutoInc)
  }

  protected class GroupTable(tag: Tag) extends BaseTable[Group](tag, "group") {
    def name = column[String]("name")

    def * = (id, name) <> (Group.tupled, Group.unapply)
  }

  protected class TeacherTable(tag: Tag) extends BaseTable[Teacher](tag, "teacher") {
    def name = column[String]("name")

    def * = (id, name) <> (Teacher.tupled, Teacher.unapply)
  }

  protected class RoomTable(tag: Tag) extends BaseTable[Room](tag, "room") {
    def name        = column[String]("name")
    def description = column[String]("description")

    def * = (id, name, description) <> (Room.tupled, Room.unapply)
  }

  protected class CourseTable(tag: Tag) extends BaseTable[Course](tag, "course") {
    def courseId = column[String]("courseId")
    def name     = column[String]("name")
    def teacher  = column[String]("teacher")
    def group    = column[String]("group")
    def start    = column[DateTime]("start")
    def end      = column[DateTime]("end")
    def parent   = column[Boolean]("parent")

    def * = (id, courseId, name, teacher, group, start, end, parent) <> (Course.tupled, Course.unapply)
  }

  protected class LessonTable(tag: Tag) extends BaseTable[Lesson](tag, "lesson") {
    def courseId = column[String]("courseId")
    def name     = column[String]("name")
    def start    = column[DateTime]("start")
    def end      = column[DateTime]("end")
    def group    = column[String]("group")
    def teacher  = column[String]("teacher")
    def room     = column[String]("room")

    def * = (id, courseId, name, start, end, group, teacher, room) <> (Lesson.tupled, Lesson.unapply)
  }

  protected class EventTable(tag: Tag) extends BaseTable[Event](tag, "event") {
    def name        = column[String]("name")
    def description = column[String]("description")
    def start       = column[DateTime]("start")
    def end         = column[DateTime]("end")
    def modified    = column[DateTime]("modified")
    def eventType   = column[String]("event_type")

    def * = (id, name, description, start, end, modified, eventType) <> (Event.tupled, Event.unapply)
  }

  protected val groups   = TableQuery[GroupTable]
  protected val teachers = TableQuery[TeacherTable]
  protected val rooms    = TableQuery[RoomTable]
  protected val courses  = TableQuery[CourseTable]
  protected val lessons  = TableQuery[LessonTable]
  protected val events   = TableQuery[EventTable]
}
