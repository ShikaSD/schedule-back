package controllers

import akka.actor.{ActorSystem, Cancellable, Props}
import com.shika.mamk.web.actors.{ParserActor, TimerActor}
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

import scala.concurrent.duration._
import scala.util.Try

object Application extends Controller {
  private lazy val system = ActorSystem("mamk")
  private lazy val formatter = DateTimeFormat.forPattern("ddMMyy")

  var scheduler: Option[Cancellable] = None

  def tick = Action {
    println("Tick received")
    val timerActor = system.actorOf(Props[TimerActor])
    scheduler = Some(system.scheduler.scheduleOnce(28 minutes, timerActor, "Tick"))
    Ok
  }

  def parser(start: String) = Action {
    val parserActor = system.actorOf(Props[ParserActor])
    val timerActor = system.actorOf(Props[TimerActor])
    parserActor ! Option(formatter.parseDateTime(start))
    timerActor ! "Tick"
    Ok
  }
}
