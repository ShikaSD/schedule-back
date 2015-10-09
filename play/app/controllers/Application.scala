package controllers

import akka.actor.{ActorSystem, Props}
import com.shika.mamk.web.actors.ParserActor
import play.api._
import play.api.mvc._

object Application extends Controller {

  private lazy val system = ActorSystem("mamk")
  implicit val bindingModule = Configuration

  def index = Action {
    val actor = system.actorOf(Props[ParserActor])
    actor ! ""
    Ok
  }
}
