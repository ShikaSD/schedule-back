package fi.shika.schedule.actors

import com.google.inject.{ImplementedBy, Inject, Singleton}
import fi.shika.schedule.persistence.model.{Group, Room}
import fi.shika.schedule.persistence.storage.{GroupStorage, RoomStorage}
import org.joda.time.format.DateTimeFormat
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future
import scala.io.Source

@ImplementedBy(classOf[ScheduleParserImpl])
trait ScheduleParser {

  def parseGroups: Future[Seq[Group]]

  def parseRooms: Future[Seq[Room]]
}

@Singleton
class ScheduleParserImpl @Inject()(
  private val groupStorage: GroupStorage,
  private val roomStorage: RoomStorage
) extends ScheduleParser {

  private val tilatDateFormat = DateTimeFormat.forPattern("yyMMddHH:mm")
  private val soleOpsDateFormat = DateTimeFormat.forPattern("dd.MM.yyyy")

  def parseGroups = {
    val namePattern = "<option value=\".*?\" >(.*?)<\\/option>".r

    val html = getHtml(GroupUrl)

    val parsedNames = namePattern.findAllIn(html).matchData
      .map(_.group(1))
      .toSeq
      .drop(1)

    groupStorage.all()
      .map { groups =>
        parsedNames.filter(s => !groups.exists(_.name == s))
          .map(s => Group(name = s))
          .foreach(groupStorage.create)

        groups
      }.foreach(
        //Delete removed names
        _.filter(s => !parsedNames.contains(s.name))
          .foreach(groupStorage.delete)
      )

    groupStorage.all()
  }

  def parseRooms = {
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

    roomStorage.all()
        .map { rooms =>
          parsed.filter(s => !rooms.exists(_ equals s))
            .foreach(roomStorage.create)

          rooms
        }.foreach(
          _.filter(s => !parsed.exists(_ equals s))
            .foreach(roomStorage.delete)
        )

    roomStorage.all()
  }
/*
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
            .map(_.create)
            .foreach {lesson =>
              addCourse(lesson)
              addTeacher(lesson)
              addRoom(lesson)
              created += 1
            }

          lessons.view.filter(s => !parsed.exists(_ equals s))
            .foreach { g =>
              g.delete
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
        case Some(x) => x.create
        case None    => println(s"No courses found in soleops with id ${lesson.courseId} and group ${lesson.group}")
      }
    }

    if (!courses.exists(_ equals newCourse))
      newCourse.create
    else
      courses.filter(_ equals newCourse)
        .map { c =>
          val start = if(c.start.get isAfter  newCourse.start.get) newCourse.start else c.start
          val end   = if(c.end.get   isBefore newCourse.end.get)   newCourse.end   else c.end

          c.copy(start = start, end = end).update
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
      .foreach(s => Teacher(name = s).create)
  }

  private def addRoom(lesson: Lesson) = {
    val parsedRooms = lesson.room.split(",")
      .map(_.replaceAll("\\s", ""))

    val rooms = Room query
      QueryParam.or(
        parsedRooms.map(QueryParam("name", _))
      )

    parsedRooms.filter(s => !rooms.exists(_.name == s))
      .foreach(s => Room(name = s).create)
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
      val start = tilatDateFormat.parseDateTime(date + time(0))
      val end = tilatDateFormat.parseDateTime(date + time(1))

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
        start = ParseDate(start),
        end = ParseDate(end),
        group = group.name,
        teacher = teacher,
        room = room
      )
    }
  }*/

  private def getHtml(url: String) = {
    Source.fromURL(url).mkString
  }
/*
  private def formUrls(name: String, start: DateTime): Array[(Int, String)] = {
    (0 to WeeksToParse) map { num =>
      (num + 1, start.plusWeeks(num))
    } map { tuple =>
      val dString = tuple._2.toString(DateTimeFormat.forPattern("yyMMdd"))
      (tuple._1, ScheduleUrl + s"$dString$dString$dString&cluokka=$name")
    } toArray
  }

  private def getCourseId(string: String) = {
    val addressPattern = "\\.\\.(.*?)\'".r
    val namePattern = "<th>Selite[\\s\\S]*?<tr[\\s\\S]*?<td>.*?<td>.*?<td>.*?<td>.*?<td>(.*?)</td>".r
    val cidPattern = "([A-Z0-9]{4,} )".r
    val url = "http://tilat.mikkeliamk.fi" + string.search(addressPattern).get

    val html = getHtml(url)
    val name = html.search(namePattern).get
    name.search(cidPattern)
      .getOrElse(name)
      .replaceAll(" ", "")
  }

  private def getCourse(lesson: Lesson) = {
    val httpClient = HttpClients.custom()
      .setSSLContext(sslContext)
      .build

    val coursePattern = (
      """<td width="1%"[\s\S]*?showmode">(.*?)</div>[\s\S]*?</td>[\s\S]*?""" +
        """<td[\s\S]*?showmode">(.*?)</div>[\s\S]*?</td>[\s\S]*?""" +
        """<td[\s\S]*?</td>[\s\S]*?""" +
        """<td[\s\S]*?>(.*?)</td>[\s\S]*?""" +
        """<td[\s\S]*?</td>[\s\S]*?""" +
        """<td[\s\S]*?</td>[\s\S]*?""" +
        """<td[\s\S]*?showmode">(.*?)</div>[\s\S]*?</td>""").r

    try {
      val request = new HttpPost(SoleOpsUrl)
      val data = new UrlEncodedFormEntity(
        Seq(
          new BasicNameValuePair("ojtunnus", lesson.courseId),
          new BasicNameValuePair("ryhma", lesson.group)
        ).asJava
      )
      request.setEntity(data)
      request.setHeader("Content-Type", "application/x-www-form-urlencoded")
      request.setHeader("Accept", "text/html")

      val body = httpClient.getResponse(request)

      for (m <- coursePattern findFirstMatchIn body)
        yield {
          val dates = (m group 3).split("-").map(s => ParseDate(soleOpsDateFormat.parseDateTime(s)))
          Course(
            courseId = m group 1,
            name = m group 2 replaceAll("\\&auml;", "ä") replaceAll("\\&ouml;", "ö"),
            start = dates(0),
            end = dates(1),
            group = m group 4,
            parent = true
          )
        }
    } finally {
      httpClient.close()
    }
  }*/
}