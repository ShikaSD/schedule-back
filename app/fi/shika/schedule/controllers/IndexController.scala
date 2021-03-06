package fi.shika.schedule.controllers

import javax.inject.Inject

import fi.shika.schedule.persistence.storage.GroupStorage
import fi.shika.schedule.startup.DatabaseChecker
import play.Environment
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

class IndexController @Inject() (
  checker: DatabaseChecker,
  groupStorage: GroupStorage,
  env: Environment
) extends BaseController(env) {

  def index = Action.async { implicit request =>
    groupStorage.count().map { result =>
      Ok(s"Currently found $result groups")
    }
  }
}
