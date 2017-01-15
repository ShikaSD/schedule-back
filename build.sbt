version := Version.app

scalaVersion := Version.scala

scalacOptions ++= Seq(
  "-feature",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-Xfatal-warnings",
  "-unchecked")

lazy val root = (project in file("."))
  .settings(libraryDependencies ++= Dependencies.common)

mainClass in(Compile, run) := Some("fi.shika.schedule.Main")
run in Compile <<= (run in Compile in root)