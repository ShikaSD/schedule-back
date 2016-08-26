package fi.shika.schedule.controllers

import javax.inject.Inject

import fi.shika.schedule.startup.DatabaseChecker
import play.api.mvc._

class App @Inject() (
  protected val checker: DatabaseChecker
) extends Controller {

  def index = Action { implicit request =>
    Ok("Got it")
  }
}
