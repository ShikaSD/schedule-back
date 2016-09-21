package fi.shika

import play.api.Logger

import scala.concurrent.{ExecutionContext, Future}

package object schedule {
  /**
    * Extension for future to chain it with failures
    */
  implicit class FutureExt[T](future: Future[T])(implicit ec: ExecutionContext) {

    private lazy val log = Logger(getClass)

    def runNext[U](f: Future[U]) = future
      .recoverWith { case e: Exception => Future.successful(log.error("Future failed with", e)) }
      .flatMap(u => f)
  }

  //Amount of parsing
  val DefaultWeekToParse = 24
  val ShortWeekToParse   = 2
}
