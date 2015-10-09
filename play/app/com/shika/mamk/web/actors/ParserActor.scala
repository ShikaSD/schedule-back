package com.shika.mamk.web.actors

import akka.actor.Actor
import com.escalatesoft.subcut.inject.{BindingModule, Injectable}
import com.shika.mamk.parser.parser.ScheduleParser
import com.shika.mamk.web.util.Configuration
import play.api.libs.concurrent.Execution.Implicits.defaultContext

import scala.concurrent.Future

class ParserActor extends Actor
  with Injectable {

  implicit val bindingModule: BindingModule = Configuration
  private lazy val schedule = inject[ScheduleParser]

  private def parse() = {
    Future( schedule parseRooms )

    schedule.parseGroups foreach schedule.parseLessons
  }

  override def receive = {
    case _ => parse()
  }
}
