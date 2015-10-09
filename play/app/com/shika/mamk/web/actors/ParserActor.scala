package com.shika.mamk.web.actors

import akka.actor.Actor
import com.escalatesoft.subcut.inject.{Injectable, BindingModule}
import com.shika.mamk.parser.parser.ScheduleParser

import scala.concurrent.Future
import play.api.libs.concurrent.Execution.Implicits.defaultContext

class ParserActor(implicit val bindingModule: BindingModule) extends Actor
  with Injectable {

  private lazy val schedule = inject[ScheduleParser]

  private def parse() = {
    Future( schedule parseRooms )

    schedule.parseGroups foreach schedule.parseLessons
  }

  override def receive = {
    case _ => parse()
  }
}
