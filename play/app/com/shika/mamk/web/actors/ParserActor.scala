package com.shika.mamk.web.actors

import akka.actor.Actor
import com.escalatesoft.subcut.inject.{BindingModule, Injectable}
import com.shika.mamk.parser.service.{ScheduleParser, StudentParser}
import com.shika.mamk.web.util.Configuration
import controllers.Application
import org.joda.time.{DateTime, DateTimeConstants}
import play.api.Logger
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

class ParserActor extends Actor
  with Injectable {

  implicit val bindingModule: BindingModule = Configuration
  private lazy val scheduleParser = inject[ScheduleParser]
  private lazy val student  = inject[StudentParser]

  private lazy val log = Logger(getClass.getName)

  private def parse(start: DateTime) = {
    implicit val startDate = start.withDayOfWeek(DateTimeConstants.MONDAY)

    log.info("Parser started")

    //Parsing in parallel threads
    val schedule = Future {
      scheduleParser.parseGroups foreach {g =>
        val (added, deleted) = scheduleParser.parseLessons(g)
        log.info(s"Parsed lessons for group ${g.name} added: $added, deleted $deleted")
      }
    }

    val parsing = for {
      rooms   <- Future(scheduleParser.parseRooms)
      s       <- schedule
      changes <- Future(student.parseChanges)
      events  <- Future(student.parseEvents)
    } yield None

    parsing.onSuccess {
      case _ =>
        log.info("End of parsing")
        cancelTick()
    }
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
