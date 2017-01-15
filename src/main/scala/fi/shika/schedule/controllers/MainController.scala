package fi.shika.schedule.controllers

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import com.typesafe.scalalogging.Logger
import fi.shika.schedule.persistence.model.Group
import fi.shika.schedule.persistence.storage.GroupStorageImpl
import io.circe.Encoder
import io.circe.generic.semiauto._
import io.circe.syntax._

import scala.util.{Failure, Success}

trait MainController extends BaseController {

  protected def logger: Logger

  protected lazy val groupStorage = new GroupStorageImpl()

  protected lazy val mainRoute = get {
    pathPrefix("group") {
      onComplete(groupStorage.all()) {
        case Success(groups) => complete(groups.asJson)
        case Failure(ex)     =>
          logger.error("Error while retrieving groups", ex)
          complete(InternalServerError)
      }
    }
  }

  implicit val groupEncoder: Encoder[Group] = deriveEncoder
}
