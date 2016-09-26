package fi.shika.schedule.controllers

import javax.inject.Inject

import fi.shika.schedule.persistence.storage.GroupStorage
import fi.shika.schedule.startup.DatabaseChecker
import play.Environment
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.concurrent.ExecutionContext

/**
  * Controller for operating group list
  */
class GroupController @Inject() (
  checker: DatabaseChecker,
  groupStorage: GroupStorage,
  env: Environment
)(implicit val ec: ExecutionContext) extends BaseController(env) {

  def all = withId(Action.async {
    groupStorage.all().map { groups =>
      Ok(Json.toJson(groups))
    }
  })
}
