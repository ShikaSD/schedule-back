import play.sbt.PlayImport.jdbc
import sbt._

object Version {
  lazy val scala                = "2.11.8"
  lazy val app                  = "0.1"

  lazy val playSlick            = "2.0.0"
  lazy val postgresDriver       = "9.4.1209.jre7"
  lazy val jodaMapper           = "2.2.0"

  lazy val akka                 = "2.4.9"
  lazy val json4s               = "3.3.0.RC6"
  lazy val time                 = "2.12.0"
}

object Libraries {

  lazy val json4s               = "org.json4s"                     %% "json4s-native"          % Version.json4s
  lazy val json4sExt            = "org.json4s"                     %% "json4s-ext"             % Version.json4s
  lazy val time                 = "com.github.nscala-time"         %% "nscala-time"            % Version.time

  lazy val akkaHttp             = "com.typesafe.akka"              %% "akka-http-experimental" % Version.akka
  lazy val akkaSlf4j            = "com.typesafe.akka"              %% "akka-slf4j"             % Version.akka

  lazy val playSlick            = "com.typesafe.play"              %% "play-slick"             % Version.playSlick
  lazy val jodaMapper           = "com.github.tototoshi"           %% "slick-joda-mapper"      % Version.jodaMapper
  lazy val postgresDriver       = "org.postgresql"                 %  "postgresql"             % Version.postgresDriver

}

object Dependencies {
  import Libraries._

  val slick = Seq(playSlick, postgresDriver, jodaMapper)

  val akka = Seq(akkaHttp, akkaSlf4j)

  val common = Seq(time, jdbc) ++ akka ++ slick .map( _.exclude("com.zaxxer", "HikariCP-java6"))
}