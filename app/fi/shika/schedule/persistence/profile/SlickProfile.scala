package fi.shika.schedule.persistence.profile

import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile


trait SlickProfile {
  protected val configProvider: DatabaseConfigProvider

  protected lazy val dbConfig = configProvider.get[JdbcProfile]
  protected lazy val db       = dbConfig.db
  protected lazy val driver   = dbConfig.driver
}
