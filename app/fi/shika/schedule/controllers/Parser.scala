package fi.shika.schedule.controllers

import javax.inject.Inject

import akka.actor.{ActorRef, ActorSystem, Cancellable}
import com.google.inject.name.Named
import fi.shika.schedule.actors.ParserActor.Parse
import org.joda.time.DateTime
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class Parser @Inject() (
  @Named("parser-actor") parser: ActorRef,
  actorSystem: ActorSystem
)(implicit val ec: ExecutionContext) extends Controller {

  private lazy val parserFrequency = 24.hours
  private var lastParser: Option[Cancellable] = None

  def parseSchedule = Action { implicit request =>
    val timeToMidnight = DateTime.now.withTimeAtStartOfDay.plusDays(1).getMillis - DateTime.now.getMillis

    lastParser.foreach(_.cancel())

    lastParser = Some(actorSystem.scheduler.schedule(
      Duration(timeToMidnight, MILLISECONDS),
      parserFrequency,
      parser,
      Parse()))

    Ok(s"Parser will start at midnight and will be running every $parserFrequency")
  }
}
