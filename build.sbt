import sbt._

lazy val commonSettings = Seq(
  version := Versions.app,
  scalaVersion := Versions.scala
)

lazy val parser = (project in file ("parser"))
  .settings(name := "Parser")
  .settings(commonSettings: _*)
  .settings(
      libraryDependencies ++= Dependencies.common,
      libraryDependencies ++= Dependencies.rest
  )