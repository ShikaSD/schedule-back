import sbt._

lazy val commonSettings = Seq(
  version := Version.app,
  scalaVersion := Version.scala,
  libraryDependencies ++= Dependencies.common,
  scalacOptions ++= Seq(
    "-feature",
    "-language:postfixOps",
    "-Xfatal-warnings",
    "-unchecked"
  )
)

lazy val rest = (project in file ("rest"))
  .settings(name := "rest")
  .settings(commonSettings: _*)
  .settings(
      libraryDependencies ++= Dependencies.rest
  )

lazy val parser = (project in file("parser"))
  .settings(name := "parser")
  .settings(commonSettings: _*)
  .dependsOn(rest)

lazy val play = (project in file("play"))
  .settings(name := "play")
  .settings(commonSettings: _*)
  .enablePlugins(PlayScala)
  .settings(libraryDependencies ++= Dependencies.web)
  .settings(libraryDependencies +=  cache)
  .dependsOn(parser)

lazy val root = (project in file("."))
  .settings(run   in Compile <<= (run   in Compile) in play)
  .settings(stage in Compile <<= (stage in Compile) in play)