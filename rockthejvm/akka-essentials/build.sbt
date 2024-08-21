name := "akka-essentials"

version := "0.1"

scalaVersion := "2.13.14"

val akkaVersion = "2.5.23"

val PekkoVersion = "1.0.2"

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor-typed" % PekkoVersion,
  "org.apache.pekko" %% "pekko-testkit" % PekkoVersion
//  "org.scalatest" %% "scalatest" % "3.0.5"
)
