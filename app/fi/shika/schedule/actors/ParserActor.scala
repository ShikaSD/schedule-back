package fi.shika.schedule.actors

import akka.actor.Actor
import com.google.inject.Inject
import fi.shika.schedule.actors.ParserActor.Parse
import org.joda.time.{DateTime, DateTimeConstants}
import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

object ParserActor {

  case class Parse(date: DateTime = DateTime.now)
}

class ParserActor @Inject()(private val scheduleParser: ScheduleParser)(implicit ec: ExecutionContext) extends Actor {

  private lazy val log = Logger(getClass.getName)

  private def parse(start: DateTime) = {
    implicit val startDate = start.withDayOfWeek(DateTimeConstants.MONDAY)

    log.info(s"Parser started at $startDate")

    //Parsing in parallel threads
    val schedule = Future {
      log.info("Parsing groups and lessons...")
    } flatMap { base =>
      scheduleParser.parseGroups flatMap { groups =>
        Future sequence groups.map { g =>
          val future = scheduleParser.parseLessons(g)
          future.onSuccess { case (added: Int, deleted: Int) =>
            log.info(s"Parsed lessons for group $g added: $added, deleted $deleted")
          }

          future
        }
      }
    }
    /*val changes = Future {
      log.info("Parsing changes...")
      studentParser.parseChanges
      log.info("Changes parsed")
    }
    val events  = Future {
      log.info("Parsing events...")
      studentParser.parseEvents
      log.info("Events parsed")
    }*/
    val rooms = Future(log.info("Parsing rooms..."))
      .flatMap(s => scheduleParser.parseRooms)
      .map(s => log.info("Rooms parsed"))

    val future = for {
      s <- schedule
      r <- rooms
    } yield (s, r)

    future.onComplete(s => log.info("End of parsing"))
  }

  override def receive = {
    case Parse(x: DateTime) =>
      parse(x)
  }
}