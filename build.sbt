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
  .settings(resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/maven-releases/")
  .settings(libraryDependencies ++= Dependencies.common)