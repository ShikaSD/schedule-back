package fi.shika.schedule.parser

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.{Materializer, scaladsl}
import akka.stream.scaladsl.Sink
import com.google.inject.{ImplementedBy, Inject, Singleton}
import fi.shika.schedule.persistence.model._
import fi.shika.schedule.persistence.storage._
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.Logger

import scala.collection.immutable
import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source

@ImplementedBy(classOf[ScheduleParserImpl])
trait ScheduleParser {

  def parseGroups: Future[Seq[Group]]

  def parseRooms: Future[Seq[Room]]

  def parseLessons(group: Group)(implicit startDate: DateTime): Future[(Int, Int)]
}

@Singleton
class ScheduleParserImpl @Inject()(
  private val groupStorage  : GroupStorage,
  private val roomStorage   : RoomStorage,
  private val lessonStorage : LessonStorage,
  private val courseStorage : CourseStorage,
  private val teacherStorage: TeacherStorage
)(
  implicit val ec: ExecutionContext,
  implicit val system: ActorSystem,
  implicit val materializer: Materializer
) extends ScheduleParser {

  private lazy val log = Logger(getClass)

  private lazy val tilatDateFormat   = DateTimeFormat.forPattern("yyMMddHH:mm")
  private lazy val soleOpsDateFormat = DateTimeFormat.forPattern("dd.MM.yyyy")
  private def soleOpsFlow = Http().outgoingConnection(SoleOpsUrl)

  def parseGroups = {
    val namePattern = "<option value=\".*?\" >(.*?)<\\/option>".r

    val html = getHtml(GroupUrl)

    val parsedNames = namePattern.findAllIn(html).matchData
      .map(_.group(1))
      .toSeq
      .drop(1)

    groupStorage.all()
      .flatMap { groups =>
        val toCreate = parsedNames.filter(s => !groups.exists(_.name == s))
          .map(s => Group(name = s))

        val toDelete = groups.filter(s => !parsedNames.contains(s.name))

        groupStorage.createAll(toCreate)
          .flatMap(f => groupStorage.deleteAll(toDelete))
      }.flatMap(s => groupStorage.all())
  }

  def parseRooms = {
    val namePattern = "<option value=\".*?\" >(.*?)<\\/option>".r
    val html = RoomUrls.fold("")((html, s) => html + getHtml(s))

    val parsed = namePattern.findAllIn(html).matchData
      .map(_.group(1))
      .toSeq
      .filter(!_.contains("Valitse"))
      .map { s =>
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
        val toCreate = parsed.filter(s => !rooms.exists(_ sameAs s))
        val toDelete = rooms.filter(s => !parsed.exists(_ sameAs s))

        roomStorage.createAll(toCreate)
          .flatMap(s => roomStorage.deleteAll(toDelete))
      }.flatMap(s => roomStorage.all())
  }

  def parseLessons(group: Group)(implicit startDate: DateTime) = {
    var deleted = 0
    var created = 0

    val parsedFutures = formUrls(group.name, startDate) map { case (weekNum, url) =>
      lessonStorage.groupLessonsBetween(
        group.name,
        startDate plusWeeks weekNum - 1,
        startDate plusWeeks weekNum
      ).flatMap { lessons =>
        val parsed = parseWeek(url, group).toList

        val lessonsToCreate = parsed.filter(l => !lessons.exists(_ sameAs l))
        val lessonsToDelete = lessons.filter(l => !parsed.exists(_ sameAs l))

        val teachersToCreate = lessonsToCreate.flatMap(_.teachers).distinct
        val roomsToCreate    = lessonsToCreate.flatMap(_.rooms).distinct

        val createdFuture = lessonStorage.createAll(lessonsToCreate)
          .map(f => created += lessonsToCreate.length)

        val deletedFuture = lessonStorage.deleteAll(lessonsToDelete)
          .map(f => deleted += lessonsToDelete.length)

        createdFuture
          .flatMap(f => deletedFuture)
          .flatMap(f => addTeachers(teachersToCreate))
          .flatMap(f => addRooms(roomsToCreate))
          .flatMap(f => Future sequence lessonsToCreate.map(addCourse))
      }
    } toList

    parsedFutures.reduce[Future[Any]] { case (memo, future) => memo.flatMap(s => future) }
      .map(result => (created, deleted))
  }


  private def addCourse(lesson: Lesson) = {
    courseStorage.byCourseId(lesson.courseId).flatMap { courses =>
      val parentCourseFuture = if (courses.isEmpty) {
        //Create parent course
        getCourse(lesson).flatMap {
          case Some(x) => courseStorage.create(x)
          case _ => Future.successful {
            log.info(s"No courses found in soleops with id ${lesson.courseId} and group ${lesson.group}")
          }
        }
      } else {
        Future.successful {
          log.info(s"${courses.size} found in database for id ${lesson.courseId}")
        }
      }

      parentCourseFuture.map(s => courses)
    }.flatMap { courses =>

      val newCourse = Course(
        courseId = lesson.courseId,
        name = lesson.name,
        teachers = lesson.teachers,
        group = lesson.group,
        start = lesson.start,
        end = lesson.end)

      if (!courses.exists(_ sameAs newCourse)) {
        courseStorage.create(newCourse)
      } else {
        val sameCourse = courses.filter(_ sameAs newCourse).head

        val start = if (sameCourse.start isAfter newCourse.start) newCourse.start else sameCourse.start
        val end =   if (sameCourse.end isBefore newCourse.end)    newCourse.end   else sameCourse.end

        courseStorage.update(sameCourse.copy(start = start, end = end))
          .flatMap { s =>
            courseStorage.deleteAll(courses.filter(_ sameAs newCourse).tail)
          }
      }
    }
  }


  private def addTeachers(teacherNames: Seq[String]) = {
    teacherStorage.byNames(teacherNames).flatMap { teachers =>
      teacherStorage.createAll(
        teacherNames.filter(name => !teachers.exists(_.name == name))
          .map(name => Teacher(name = name))
      )
    }
  }

  private def addRooms(roomNames: Seq[String]) = {
    roomStorage.byNames(roomNames).flatMap(rooms =>
      roomStorage.createAll(
        roomNames.filter(s => !rooms.exists(_.name == s))
          .map(s => Room(name = s))
      )
    )
  }

  private def parseWeek(url: String, group: Group) = {
    val lessonPattern  = "(onclick=.*?&pvm[\\s\\S]*?</td>)".r
    val infoPattern    = "<b>(.*)".r
    val datePattern    = "&pvm=(.*?)&".r
    val roomPattern    = "</b>(.*?)</font>".r
    val teacherPattern = "</a>(.*)".r
    val cidPattern     = "([A-Z0-9]{4,} )".r
    val junkPattern    = "(^[\\s\"]+|[\\s,.]+$)".r
    val extraPattern   = "(<.*?>|\"|\\n)".r

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
      val rooms = part.search(roomPattern).getOrElse("")
        .replaceAll(extraPattern.regex, "")
        .replaceAll(junkPattern.regex, "")
        .split(",")
        .map(_.replaceAll("([\\s]+$|^[\\s]+)", ""))
        .filter(!_.isEmpty)
        .distinct
        .toList

      val teachers = part.search(teacherPattern).getOrElse("")
        .replaceAll(extraPattern.regex, "")
        .replaceAll(junkPattern.regex, "")
        .split(",")
        .map(_.replaceAll("([\\s]+$|^[\\s]+)", ""))
        .filter(!_.isEmpty)
        .distinct
        .toList

      //Create lesson
      Lesson(
        courseId = cid,
        name     = name,
        start    = start,
        end      = end,
        group    = group.name,
        teachers  = teachers,
        rooms     = rooms
      )
    }
  }

  private def getHtml(url: String) = {
    Source.fromURL(url).mkString
  }


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

    val coursePattern = (
      """<td width="1%"[\s\S]*?showmode">(.*?)</div>[\s\S]*?</td>[\s\S]*?""" +
        """<td[\s\S]*?showmode">(.*?)</div>[\s\S]*?</td>[\s\S]*?""" +
        """<td[\s\S]*?</td>[\s\S]*?""" +
        """<td[\s\S]*?>(.*?)</td>[\s\S]*?""" +
        """<td[\s\S]*?</td>[\s\S]*?""" +
        """<td[\s\S]*?</td>[\s\S]*?""" +
        """<td[\s\S]*?showmode">(.*?)</div>[\s\S]*?</td>""").r

    val data = FormData(
      "ojtunnus" -> lesson.courseId,
      "ryhma"    -> lesson.group
    )

    val headers = immutable.Seq(
      Accept(MediaRange(MediaTypes.`text/html`))
    )

    val resultFuture = for {
      response <- scaladsl.Source.single(
        HttpRequest(
          method = HttpMethods.POST,
          uri = SoleOpsPath,
          entity = data.toEntity,
          headers = headers)
      ) .via(soleOpsFlow)
        .runWith(Sink.head)
      result <- Unmarshal(response).to[String]
    } yield result

    resultFuture.map { body =>
      for (m <- coursePattern findFirstMatchIn body)
        yield {
          val dates = (m group 3).split("-").map(soleOpsDateFormat.parseDateTime)
          Course(
            courseId = m group 1,
            name     = m group 2 replaceAll("\\&auml;", "ä") replaceAll("\\&ouml;", "ö"),
            start    = dates(0),
            end      = dates(1),
            group    = m group 4,
            parent   = true
          )
        }
    }
  }
}