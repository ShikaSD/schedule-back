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
  private lazy val studentParser  = inject[StudentParser]

  private lazy val log = Logger(getClass.getName)

  private def parse(start: DateTime) = {
    implicit val startDate = start.withDayOfWeek(DateTimeConstants.MONDAY)

    log.info("Parser started")

    //Parsing in parallel threads
    val schedule = Future {
      log.info("Parsing groups and lessons...")
      scheduleParser.parseGroups foreach {g =>
        val (added, deleted) = scheduleParser.parseLessons(g)
        log.info(s"Parsed lessons for group ${g.name} added: $added, deleted $deleted")
      }
      log.info("Groups and lessons parsed")
    }
    val changes = Future {
      log.info("Parsing changes...")
      studentParser.parseChanges
      log.info("Changes parsed")
    }
    val events  = Future {
      log.info("Parsing events...")
      studentParser.parseEvents
      log.info("Events parsed")
    }
    val rooms   = Future {
      log.info("Parsing rooms...")
      scheduleParser.parseRooms
      log.info("Rooms parsed")
    }

    Future.sequence(
      Seq( schedule, changes, events, rooms )
    ).onComplete({
      s =>
        log.info("End of parsing")
        cancelTick()
    })
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
