import sbt._

object Version {
  lazy val scala                = "2.11.8"
  lazy val app                  = "0.2"

  lazy val time                 = "2.12.0"

  lazy val slick                = "3.0.0"
  lazy val hikari               = "2.4.9"
  lazy val postgresDriver       = "9.4.1209.jre7"
  lazy val jodaMapper           = "2.2.0"

  lazy val config               = "1.3.1"

  lazy val akka                 = "10.0.1"
  lazy val akkaJson             = "1.11.0"
  lazy val circe                = "0.6.1"

  lazy val guice                = "4.1.0"
  lazy val logging              = "3.5.0"
  lazy val logback              = "1.1.7"
}

object Libraries {

  lazy val time                 = "com.github.nscala-time"         %% "nscala-time"            % Version.time

  lazy val akkaHttp             = "com.typesafe.akka"              %% "akka-http"              % Version.akka
  lazy val akkaJson             = "de.heikoseeberger"              %% "akka-http-circe"        % Version.akkaJson

  lazy val slick                = "com.typesafe.slick"             %% "slick"                  % Version.slick
  lazy val hikari               = "com.zaxxer"                     %  "HikariCP-java7"         % Version.hikari
  lazy val jodaMapper           = "com.github.tototoshi"           %% "slick-joda-mapper"      % Version.jodaMapper
  lazy val postgresDriver       = "org.postgresql"                 %  "postgresql"             % Version.postgresDriver

  lazy val config               = "com.typesafe"                   %  "config"                 % Version.config

  lazy val circeCore            = "io.circe"                       %% "circe-core"             % Version.circe
  lazy val circeGeneric         = "io.circe"                       %% "circe-generic"          % Version.circe
  lazy val circeParser          = "io.circe"                       %% "circe-parser"           % Version.circe

  lazy val guice                = "com.google.inject"              %  "guice"                  % Version.guice
  lazy val logging              = "com.typesafe.scala-logging"     %% "scala-logging"          % Version.logging
  lazy val logback              = "ch.qos.logback"                 %  "logback-classic"        % Version.logback
}

object Dependencies {
  import Libraries._

  val database = Seq(slick, postgresDriver, jodaMapper, hikari)

  val akka = Seq(akkaHttp, akkaJson)

  val circe = Seq(circeCore, circeGeneric, circeParser)

  val common = Seq(time, config, guice, logging, logback) ++ akka ++ database ++ circe
}