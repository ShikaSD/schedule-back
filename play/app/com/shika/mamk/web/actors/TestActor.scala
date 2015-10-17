package com.shika.mamk.web.actors

import akka.actor.Actor
import akka.actor.Actor.Receive
import com.shika.mamk.web.util.Configuration

class TestActor extends Actor {

  implicit val bindingModule = Configuration
  val testService = inject[TestService]

  override def receive: Receive = {
    case _ =>
  }
}
