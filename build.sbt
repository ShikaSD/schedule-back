import sbt._

lazy val commonSettings = Seq(
  version := Version.app,
  scalaVersion := Version.scala,
  libraryDependencies ++= Dependencies.common
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