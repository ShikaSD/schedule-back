package fi.shika.schedule.startup

import com.typesafe.scalalogging.Logger
import fi.shika.schedule.persistence.TableComponent
import fi.shika.schedule.persistence.profile.SlickProfile
import slick.jdbc.meta.MTable

import scala.concurrent.ExecutionContext

trait DatabaseChecker extends TableComponent with SlickProfile {

  protected def logger: Logger

  implicit def ec: ExecutionContext

  import driver.api._

  protected def checkTables() {

    val tables = Seq(groups, teachers, rooms, courses, lessons, events)

    db.run(MTable.getTables) map { existingTables =>
      tables foreach { table =>
        logger.info(s"Checking table named ${table.baseTableRow.tableName}")

        if (!existingTables.exists(_.name.name == table.baseTableRow.tableName)) {
          logger.info(s"Creating table named ${table.baseTableRow.tableName}")

          db.run(table.schema.create)
        }
      }
    }

  }
}