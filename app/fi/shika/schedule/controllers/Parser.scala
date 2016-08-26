package fi.shika.schedule.controllers

import javax.inject.Inject

import akka.actor.ActorRef
import com.google.inject.name.Named
import fi.shika.schedule.actors.ParserActor.Parse
import play.api.Logger
import play.api.mvc.{Action, Controller}


class Parser @Inject() (
  @Named("parser-actor") parser: ActorRef
) extends Controller {

  private lazy val logger = Logger(getClass)

  def parseSchedule = Action { implicit request =>
    parser ! Parse()

    Ok("Parser started")
  }
}
