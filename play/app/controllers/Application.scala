package controllers

import akka.actor.{ActorSystem, Cancellable, Props}
import com.shika.mamk.web.actors.{ParserActor, TimerActor}
import play.api._
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

import scala.concurrent.duration._

object Application extends Controller {

  private lazy val system = ActorSystem("mamk")
  var scheduler: Option[Cancellable] = None

  implicit val bindingModule = Configuration

  def tick = Action {
    println("Tick received")
    val timerActor = system.actorOf(Props[TimerActor])
    scheduler = Some(system.scheduler.scheduleOnce(28 minutes, timerActor, "Tick"))
    Ok
  }

  def parser = Action {
    val actor = system.actorOf(Props[ParserActor])
    val timerActor = system.actorOf(Props[TimerActor])
    actor ! ""
    timerActor ! "Tick"
    Ok
  }
}
