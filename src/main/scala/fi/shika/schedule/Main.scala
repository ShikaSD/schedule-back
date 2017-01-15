package fi.shika.schedule

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import fi.shika.schedule.controllers.MainController
import fi.shika.schedule.startup.DatabaseChecker

import scala.util.{Failure, Success}

object Main extends App with DatabaseChecker with LazyLogging with MainController {

  private val SystemName = "schedule"
  private val Host       = "localhost"
  private val Port       = 9000

  implicit val system       = ActorSystem(SystemName)
  implicit val materializer = ActorMaterializer()
  implicit val ec           = system.dispatcher

  private val bindingFuture = Http().bindAndHandle(mainRoute, Host, Port)
  bindingFuture.onComplete {
    case Success(_) =>
      logger.info("Server started.")
      checkTables()
    case Failure(ex) =>
      logger.error("Server start failure", ex)
      shutdown()
  }

  private def shutdown() {
    bindingFuture
      .flatMap(_.unbind())
      .onComplete(_ => system.terminate())
  }
}
