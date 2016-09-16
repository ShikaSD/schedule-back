package fi.shika.schedule.controllers

import javax.inject.Inject

import akka.actor.{ActorRef, ActorSystem}
import com.google.inject.name.Named
import fi.shika.schedule.actors.ParserActor.Parse
import play.api.mvc.{Action, Controller}

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._

class Parser @Inject() (
  @Named("parser-actor") parser: ActorRef,
  actorSystem: ActorSystem
)(implicit val ec: ExecutionContext) extends Controller {

  private lazy val parserFrequency = 6.hours

  def parseSchedule = Action { implicit request =>
    actorSystem.scheduler.schedule(
      0.milliseconds,
      parserFrequency,
      parser,
      Parse())

    Ok(s"Parser started and will be running every $parserFrequency")
  }
}
