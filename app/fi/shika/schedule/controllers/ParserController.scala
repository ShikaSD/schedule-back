package fi.shika.schedule.controllers

import javax.inject.Inject

import akka.actor.{ActorRef, ActorSystem, Cancellable}
import com.google.inject.name.Named
import fi.shika.schedule._
import fi.shika.schedule.actors.ParserActor.Parse
import org.joda.time.DateTime
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class ParserController @Inject() (
  @Named("parser-actor") parser: ActorRef,
  actorSystem: ActorSystem
)(implicit val ec: ExecutionContext) extends Controller {

  private lazy val largeParserFrequency = 24.hours
  private lazy val smallParserFrequency = 4.hours
  private var largeParser: Option[Cancellable] = None
  private var smallParser: Option[Cancellable] = None

  def parseSchedule = Action { implicit request =>
    val timeToMidnight = DateTime.now.withTimeAtStartOfDay.plusDays(1).getMillis - DateTime.now.getMillis

    largeParser.foreach(_.cancel())
    smallParser.foreach(_.cancel())

    largeParser = Some(actorSystem.scheduler.schedule(
      Duration(timeToMidnight, MILLISECONDS),
      largeParserFrequency,
      parser,
      Parse()))

    smallParser = Some(actorSystem.scheduler.schedule(
      Duration(timeToMidnight, MILLISECONDS) + 4.hours,
      smallParserFrequency,
      parser,
      Parse(weekAmount = ShortWeekToParse)))

    Ok(
      s"Parser will start at midnight and will be running every $largeParserFrequency\n" +
      s"Small parser will be running every $smallParserFrequency after midnight"
    )
  }
}
