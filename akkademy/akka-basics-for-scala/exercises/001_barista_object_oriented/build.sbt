ThisBuild / name := "coffee-house"

ThisBuild / version := "0.0.3"

ThisBuild / scalaVersion := "2.13.14"

resolvers += "Akka library repository".at("https://repo.akka.io/maven")

lazy val akkaVersion = "2.9.4"
lazy val scalatestVersion = "3.1.4"

// Run in a separate JVM, to make sure sbt waits until all threads have
// finished before returning.
// If you want to keep the application running while executing other
// sbt tasks, consider https://github.com/spray/sbt-revolver/
fork := true

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor-typed" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.11",
  "com.typesafe.akka" %% "akka-actor-testkit-typed" % akkaVersion % Test,
  "org.scalatest" %% "scalatest" % "3.1.4" % Test,
)

// Better monadic for-comprehensions
// addCompilerPlugin(CompilerPlugins.betterForComp)
