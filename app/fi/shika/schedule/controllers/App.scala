package fi.shika.schedule.controllers

import javax.inject.Inject

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.unmarshalling.Unmarshal
import akka.stream.Materializer
import fi.shika.schedule.actors._
import fi.shika.schedule.startup.DatabaseChecker
import play.api.libs.concurrent.Execution.Implicits.defaultContext
import play.api.mvc._

import scala.collection.immutable.Seq

class App @Inject() (checker: DatabaseChecker)(
  implicit val system: ActorSystem,
  implicit val materializer: Materializer
) extends Controller {

  def index = Action.async { implicit request =>
    val data = FormData(
      "ryhma" -> "T5614SN"
    )

    val headers = Seq(
      Accept(MediaRange(MediaTypes.`text/html`))
    )

    val content = for {
      response <- Http().singleRequest(
        HttpRequest(
          method = HttpMethods.POST,
          uri = SoleOpsUrl,
          entity = data.toEntity,
          headers = headers))
      result <- Unmarshal(response).to[String]
    } yield result

    content map { s => Ok("Done") }
  }
}
