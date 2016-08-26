version := Version.app

scalaVersion := Version.scala

scalacOptions ++= Seq(
  "-feature",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-Xfatal-warnings",
  "-unchecked")

lazy val root = (project in file("."))
  .enablePlugins(PlayScala)
  .settings(libraryDependencies ++= Dependencies.common)