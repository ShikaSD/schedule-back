import sbt._

object Version {
  lazy val scala                = "2.11.7"
  lazy val app                  = "0.1"

  lazy val subcut               = "2.1"
  lazy val retrofit             = "1.9.0"
  lazy val json4s               = "3.3.0.RC6"
  lazy val jodaTime             = "2.8.2"
}

object Libraries {
  lazy val subcut               = "com.escalatesoft.subcut"             %%  "subcut"              % Version.subcut

  lazy val retrofit             = "com.squareup.retrofit"               %   "retrofit"            % Version.retrofit

  lazy val json4s               = "org.json4s"                          %%  "json4s-native"       % Version.json4s
  lazy val json4sExt            = "org.json4s"                          %%  "json4s-ext"          % Version.json4s
  lazy val jodaTime             = "joda-time"                           %   "joda-time"           % Version.jodaTime

  lazy val httpClient           = "org.apache.httpcomponents"           % "httpclient"            % "4.5.1"
}

object Dependencies {
  import Libraries._

  val common = Seq (
    jodaTime,
    httpClient
  )

  val rest = Seq (
    retrofit,
    json4s,
    json4sExt
  )
}