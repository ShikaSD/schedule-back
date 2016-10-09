package fi.shika.schedule.controllers

import javax.inject.Inject

import fi.shika.schedule.persistence.model.Teacher
import fi.shika.schedule.persistence.storage.TeacherStorage
import fi.shika.schedule.startup.DatabaseChecker
import play.Environment
import play.api.libs.json.Json
import play.api.mvc.Action

import scala.concurrent.ExecutionContext

/**
  * Controller for operations with teacher list
  */
class TeacherController @Inject() (
  checker        : DatabaseChecker,
  teacherStorage : TeacherStorage,
  env            : Environment
)(implicit val ec: ExecutionContext) extends BaseController(env) {

  def all = withId(Action.async {
    teacherStorage.all().map { teachers =>
      val uniqueTeachers = teachers.foldLeft(Nil: List[Teacher]) { case (acc, next) =>
          if(acc exists(next.name == _.name)) acc else next :: acc
      } reverse

      Ok(Json.toJson(uniqueTeachers))
    }
  })
}
