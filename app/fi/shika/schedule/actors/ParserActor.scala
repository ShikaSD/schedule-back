package fi.shika.schedule.actors

import akka.actor.Actor
import akka.pattern.after
import com.google.inject.Inject
import fi.shika.schedule.actors.ParserActor.Parse
import fi.shika.schedule.parser.ScheduleParser
import org.joda.time.{DateTime, DateTimeConstants}
import play.api.Logger

import scala.concurrent.duration.DurationInt
import scala.concurrent.{ExecutionContext, Future}
import scala.util.{Failure, Success}

object ParserActor {

  case class Parse(date: DateTime = DateTime.now)
}

class ParserActor @Inject()(
  private val scheduleParser: ScheduleParser
)(implicit ec: ExecutionContext) extends Actor {

  private lazy val log = Logger(getClass.getName)
  private val DebounceValue = 20.seconds

  private def parse(start: DateTime) = {
    implicit val startDate = start
      .withMinuteOfHour(0)
      .withHourOfDay(1)
      .withDayOfWeek(DateTimeConstants.MONDAY)

    log.info(s"Parser started at $startDate")

    //Parsing in parallel threads
    val schedule = Future {
      log.info("Parsing groups and lessons...")
    } flatMap { base =>
      scheduleParser.parseGroups flatMap { groups =>
        log.info(s"Parsing lessons for ${groups.length} groups")
        groups.zipWithIndex.map { case (g, i) =>
          log.info(s"Delaying parser start of group ${g.name} to ${DebounceValue * i}")
          after(DebounceValue * i, context.system.scheduler) {
            scheduleParser.parseLessons(g).andThen {
              case Success((added, deleted)) =>
                log.info(s"Parsed lessons for group $g added: $added, deleted $deleted")
              case Failure(e) =>
                log.error(s"Failed to parse lessons for group $g with exception: ", e)
            }
          }
        } reduce[Future[(Int, Int)]] { case (memo, it) =>
            memo.flatMap(s => it)
        }
      }
    }

    val rooms = Future(log.info("Parsing rooms..."))
      .flatMap(s => scheduleParser.parseRooms)
      .map(s => log.info("Rooms parsed"))

    schedule.onFailure { case e: Throwable => log.error("Schedule parsing failed with exception: ", e) }
    rooms.onFailure { case e: Throwable => log.error("Room parsing failed with exception: ", e) }

    val parsing = for {
      s <- schedule
      r <- rooms
    } yield (s, r)

    parsing.onComplete { s =>
      log.info("End of parsing")
    }
  }

  override def receive = {
    case Parse(x: DateTime) => parse(x)
  }
}