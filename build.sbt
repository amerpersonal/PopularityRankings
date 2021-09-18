name := "PopularityRankings"

version := "0.1"

scalaVersion := "2.12.4"

val akkaHttpVersion = "10.2.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-http-core" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % "test",


  "com.typesafe.akka" %% "akka-stream" % "2.6.6",

  "org.scalatest" %% "scalatest" % "3.2.9" % "test",

  "com.typesafe.akka" %% "akka-testkit" % "2.6.6" % "test",


)