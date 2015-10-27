package com.shika.mamk.parser.service

import com.escalatesoft.subcut.inject.{BindingModule, Injectable}
import com.shika.mamk.parser.helper.ParserHelper._
import com.shika.mamk.rest.AppKeys._
import com.shika.mamk.rest.RestService
import com.shika.mamk.rest.model.classes._
import com.shika.mamk.rest.model.{Param, ParseDate, QueryParam}
import org.joda.time.format.DateTimeFormat
import org.joda.time.{DateTime, DateTimeZone}

class ScheduleParserImpl(implicit val bindingModule: BindingModule)
  extends ScheduleParser with Injectable {

  private val dateFormat = DateTimeFormat.forPattern("yyMMddHH:mm")

  DateTimeZone.setDefault(DateTimeZone.forID("Europe/Helsinki"))

  override def parseGroups = {
    val namePattern = "<option value=\".*?\" >(.*?)<\\/option>".r

    val html = getHtml(GroupUrl)

    val parsedNames = namePattern.findAllIn(html).matchData
      .map(_.group(1))
      .toSeq
      .drop(1)

    keys.foreach {key =>
      RestService.initialize(key)
      val groups = Group query

      //Delete unnecessary names
      groups.view.filter(s => !parsedNames.contains(s.name))
        .foreach(_.delete())

      //Put new ones
      parsedNames.view.filter(s => !groups.exists(_.name == s))
        .map(s => Group(name = s))
        .foreach(_.create())
    }

    Group query
  }

  override def parseRooms = {
    val namePattern = "<option value=\".*?\" >(.*?)<\\/option>".r
    val html = RoomUrls.fold("")((html, s) => html + getHtml(s))

    val parsed = namePattern.findAllIn(html).matchData
      .map(_.group(1))
      .toSeq.view
      .filter(!_.contains("Valitse"))
      .map {s =>
        val restored = s.replaceAll("\\&auml;", "ä").replaceAll("\\&ouml;", "ö")
        val spacePos = restored.indexOf(" ")
        val room = Room(
          name = restored.slice(0, spacePos),
          description = restored.slice(spacePos + 1, restored.length)
        )

        if(room.name == "")
          Room(name = room.description, description = room.name)
        else
          room
      }

    keys.foreach {key =>
      RestService.initialize(key)
      val rooms = Room query

      rooms.view.filter(s => !parsed.exists(_ equals s))
        .foreach(_.delete())

      parsed.view.filter(s => !rooms.exists(_ equals s))
        .foreach(_.create())
    }

    Room query
  }

  override def parseLessons(group: Group)(implicit startDate: DateTime) = {
    var deleted = 0
    var created = 0

    formUrls(group.name, startDate) foreach {case (weekNum, url) =>
      val parsed = parseWeek(url, group)
      keys foreach {key =>
        RestService initialize key

        try {
          val lessons = Lesson query QueryParam("group", group.name)
            .add("start", Param(
              greaterThanOrEqual = ParseDate(startDate plusWeeks weekNum - 1),
              lessThan           = ParseDate(startDate plusWeeks weekNum)
            ))

          parsed.view.filter(s => !lessons.exists(_ equals s))
            .map(_.create())
            .foreach {lesson =>
              addCourse(lesson)
              addTeacher(lesson)
              addRoom(lesson)
              created += 1
            }

          lessons.view.filter(s => !parsed.exists(_ equals s))
            .foreach { g =>
              g.delete()
              deleted += 1
            }

        } catch {
          case e: Exception => e.printStackTrace()
        }
      }
    }

    (created, deleted)
  }

  private def addCourse(lesson: Lesson) = {
    val courses = Course query QueryParam("courseId", lesson.courseId)
    val newCourse = Course(
      courseId = lesson.courseId,
      name     = lesson.name,
      teacher  = lesson.teacher,
      group    = lesson.group,
      start    = lesson.start,
      end      = lesson.end
    )
    if(courses.isEmpty) {
      //Create parent course
      getCourse(lesson) match {
        case Some(x) => x.create()
        case None    => println(s"No courses found in soleops with id ${lesson.courseId} and group ${lesson.group}")
      }
    }

    if (!courses.exists(_ equals newCourse))
      newCourse.create()
    else
      courses.filter(_ equals newCourse)
        .map { c =>
          val start = if(c.start.get isAfter  newCourse.start.get) newCourse.start else c.start
          val end   = if(c.end.get   isBefore newCourse.end.get)   newCourse.end   else c.end

          c.copy(start = start, end = end).update()
        }
  }

  private def addTeacher(lesson: Lesson) = {
    val parsedTeachers = lesson.teacher.split(",")
      .map(_.replaceAll("([\\s]+$|^[\\s]+)", ""))

    val teachers = Teacher query
      QueryParam.or(
        parsedTeachers.map(QueryParam("name", _))
      )

    parsedTeachers.filter(s => !teachers.exists(_.name == s))
      .foreach(s => Teacher(name = s).create())
  }

  private def addRoom(lesson: Lesson) = {
    val parsedRooms = lesson.room.split(",")
      .map(_.replaceAll("\\s", ""))

    val rooms = Room query
      QueryParam.or(
        parsedRooms.map(QueryParam("name", _))
      )

    parsedRooms.filter(s => !rooms.exists(_.name == s))
      .foreach(s => Room(name = s).create())
  }

  private def parseWeek(url: String, group: Group) = {
    val lessonPattern = "(onclick=.*?&pvm[\\s\\S]*?</td>)".r
    val infoPattern = "<b>(.*)".r
    val datePattern = "&pvm=(.*?)&".r
    val roomPattern = "</b>(.*?)</font>".r
    val teacherPattern = "</a>(.*)".r
    val cidPattern = "([A-Z0-9]{4,} )".r
    val junkPattern = "(^[\\s\"]+|[\\s,.]+$)".r
    val extraPattern = "(<.*?>|\"|\\n)".r

    val htmlParts = lessonPattern.findAllIn(getHtml(url)).matchData
      .map(_.group(1))
      .toArray

    htmlParts map {part: String =>
      val splitParts = part.search(infoPattern).get
        .split("<br/>")
        .map(_.replaceAll(extraPattern.regex, ""))

      //Date
      val time = splitParts.head.split(" - ")
      val date = part.search(datePattern).get
      val start = dateFormat.parseDateTime(date + time(0))
      val end = dateFormat.parseDateTime(date + time(1))

      //There can be nothing in array, so skip it
      //if (splitParts.length < 2) return

      //Name
      var name = splitParts(1)
        .replaceAll(extraPattern.regex, "")
      val cid = getCourseId(part)

      name = name
        .replaceAll(cidPattern.regex, "")
        .replaceAll(junkPattern.regex, "")

      //Room and teachers
      val room = part.search(roomPattern).get
        .replaceAll(extraPattern.regex, "")
        .replaceAll(junkPattern.regex, "")

      val teacher = part.search(teacherPattern).get
        .replaceAll(extraPattern.regex, "")
        .replaceAll(junkPattern.regex, "")

      //Create lesson
      Lesson(
        courseId = cid,
        name = name,
        start = Some( ParseDate(start) ),
        end = Some( ParseDate(end) ),
        group = group.name,
        teacher = teacher,
        room = room
      )
    }
  }
}