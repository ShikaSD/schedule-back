package fi.shika.schedule.controllers


import play.Environment
import play.api.mvc.{Action, Controller}

import scala.concurrent.Future


class BaseController(env: Environment) extends Controller {
  def withId[A](action: Action[A]) = Action.async(action.parser) { request =>
    val header = request.headers.get("X-Schedule-Id")

    if(header.isEmpty && env.isProd) {
      Future.successful(Forbidden("Wrong token."))
    } else {
      implicit val id = header.getOrElse("")
      action(request)
    }
  }
}
