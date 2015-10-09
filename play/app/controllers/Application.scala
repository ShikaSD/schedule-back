package controllers

import akka.actor.{Props, ActorSystem}
import com.escalatesoft.subcut.inject.{BindingModule, Injectable}
import com.shika.mamk.web.actors.ParserActor
import play.api._
import play.api.mvc._
import play.api.cache.Cache
import play.api.Play.current

object Application extends Controller {

  private lazy val system = ActorSystem.create("mamk")

  def index = Action {
    val actor = system.actorOf(Props[ParserActor])
    actor ! ""
    Ok
  }
}
