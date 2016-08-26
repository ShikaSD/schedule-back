package fi.shika.schedule.di

import com.google.inject.AbstractModule
import fi.shika.schedule.actors.ParserActor
import fi.shika.schedule.startup.{DatabaseChecker, DatabaseCheckerImpl}
import play.api.libs.concurrent.AkkaGuiceSupport


class Module extends AbstractModule with AkkaGuiceSupport {
  override def configure() = {
    bind(classOf[DatabaseChecker])
      .to(classOf[DatabaseCheckerImpl])
      .asEagerSingleton()

    bindActor[ParserActor]("parser-actor")
  }
}
