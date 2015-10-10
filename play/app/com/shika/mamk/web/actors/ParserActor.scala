package com.shika.mamk.web.actors

import akka.actor.Actor
import com.escalatesoft.subcut.inject.{BindingModule, Injectable}
import com.shika.mamk.parser.parser.ScheduleParser
import com.shika.mamk.web.util.Configuration
import controllers.Application
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

class ParserActor extends Actor
  with Injectable {

  implicit val bindingModule: BindingModule = Configuration
  private lazy val schedule = inject[ScheduleParser]

  private def parse() = {
    Future( schedule parseRooms )

    schedule.parseGroups foreach { g =>
      val (added, deleted) = schedule.parseLessons(g)
      println(s"Parsed lessons for group ${g.name} added: $added, deleted $deleted")
    }
    println(s"End of parsing")
    cancelTick()
  }

  private def cancelTick() =
    Application scheduler match {
      case Some(x) => x.cancel()
      case _ =>
    }

  override def receive = {
    case _ => parse()
  }
}
