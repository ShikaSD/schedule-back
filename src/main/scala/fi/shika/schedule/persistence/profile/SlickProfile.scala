package fi.shika.schedule.persistence.profile

import slick.driver.PostgresDriver
import slick.jdbc.JdbcBackend.Database

trait SlickProfile {
  protected lazy val db       = Database.forConfig("default")
  protected lazy val driver   = PostgresDriver
}
