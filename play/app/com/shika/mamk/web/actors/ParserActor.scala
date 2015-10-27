package com.shika.mamk.web.actors

import akka.actor.Actor
import com.escalatesoft.subcut.inject.{BindingModule, Injectable}
import com.shika.mamk.parser.service.{ScheduleParser, StudentParser}
import com.shika.mamk.web.util.Configuration
import controllers.Application
import org.joda.time.DateTime
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

class ParserActor extends Actor
  with Injectable {

  implicit val bindingModule: BindingModule = Configuration
  private lazy val schedule = inject[ScheduleParser]
  private lazy val student  = inject[StudentParser]

  private lazy val log = Logger(getClass.getName)

  private def parse(start: DateTime) = {
    Future( schedule parseRooms )
    //TODO: Implement parsing from start

    schedule.parseGroups foreach { g =>
      val (added, deleted) = schedule.parseLessons(g)
      log.info(s"Parsed lessons for group ${g.name} added: $added, deleted $deleted")
    }

    student.parseChanges
    student.parseEvents

    log.info(s"End of parsing")
    cancelTick()
  }

  private def cancelTick() =
    Application scheduler match {
      case Some(x) => x.cancel()
      case _ =>
    }

  override def receive = {
    case Some(x: DateTime) => parse(x)
    case _                 => parse(DateTime.now)
  }
}
