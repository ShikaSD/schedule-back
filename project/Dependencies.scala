import sbt._

object Versions {
  lazy val scala = "2.11.6"
  lazy val app = "0.1"

  lazy val scaldi = "0.5.6"
  lazy val retrofit = "1.9.0"
  lazy val jodaTime = "2.8.2"
}

object Libraries {
  lazy val scaldi = "org.scaldi" %% "scaldi" % Versions.scaldi
  lazy val retrofit = "com.squareup.retrofit" % "retrofit" % Versions.retrofit
  lazy val jodaTime ="joda-time" % "joda-time" % Versions.jodaTime
}

object Dependencies {
  import Libraries._

  val common = Seq (
    scaldi,
    jodaTime
  )

  val rest = Seq (
    retrofit
  )
}