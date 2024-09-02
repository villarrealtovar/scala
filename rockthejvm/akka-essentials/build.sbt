name := "akka-essentials"

version := "0.1"

scalaVersion := "2.13.14"

val pekkoVersion = "1.0.2"
val scalaTestVersion = "3.2.9"
val logbackVersion = "1.2.11"

libraryDependencies ++= Seq(
  "org.apache.pekko" %% "pekko-actor-typed" % pekkoVersion,
  "org.apache.pekko" %% "pekko-testkit" % pekkoVersion,
  "ch.qos.logback" % "logback-classic" % logbackVersion,
  "org.scalatest" %% "scalatest" % scalaTestVersion
)
