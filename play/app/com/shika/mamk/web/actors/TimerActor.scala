package com.shika.mamk.web.actors

import akka.actor.Actor

import scala.io.Source._


class TimerActor extends Actor {
  override def receive: Receive = {
    case "Tick" =>
      fromURL("http://mamk-heroku.herokuapp.com/tick").mkString
  }
}
