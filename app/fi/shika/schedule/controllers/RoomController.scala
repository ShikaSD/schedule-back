package fi.shika.schedule.controllers

import javax.inject.Inject

import fi.shika.schedule.persistence.storage.RoomStorage
import fi.shika.schedule.startup.DatabaseChecker
import play.Environment
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.concurrent.ExecutionContext

/**
  * Controller for operations with room list
  */
class RoomController @Inject() (
  checker     : DatabaseChecker,
  roomStorage : RoomStorage,
  env         : Environment
)(implicit val ec: ExecutionContext) extends BaseController(env) {

  def all = withId(Action.async {
    roomStorage.all().map { rooms =>
      Ok(Json.toJson(rooms))
    }
  })
}