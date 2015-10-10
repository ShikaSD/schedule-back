package com.shika.mamk.web.actors

import akka.actor.Actor
import akka.actor.Actor.Receive

import scala.io.Source._


class TimerActor extends Actor {
  override def receive: Receive = {
    case "Tick" => fromURL("http://mamk-heroku.herokuapp.com/tick").mkString
  }
}
