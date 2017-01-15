package fi.shika.schedule.controllers

import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.CirceSupport

trait BaseController extends CirceSupport {
  protected def withId(f: (String => Route)) = optionalHeaderValueByName("X-Schedule") {
    case Some (userId) => f (userId)
    case _ => complete (Unauthorized)
  }
}
