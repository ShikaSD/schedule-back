package fi.shika.schedule.controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.stream.Materializer
import fi.shika.schedule.persistence.storage.GroupStorage
import fi.shika.schedule.startup.DatabaseChecker
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.libs.json.Json
import play.api.mvc._

class App @Inject() (
  checker: DatabaseChecker,
  groupStorage: GroupStorage
)(
  implicit val system: ActorSystem,
  implicit val materializer: Materializer
) extends Controller {

  def index = Action.async { implicit request =>
    groupStorage.all() map { groups =>
      Ok(Json.toJson(groups))
    }
  }
}
