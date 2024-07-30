import Dependencies._

ThisBuild / version := "0.1.0"
ThisBuild / scalaVersion := "2.13.14"
ThisBuild / organization := "javt.io"

addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.13.3" cross CrossVersion.full)


lazy val root = (project in file("."))
  .settings(
    name := "workshop-fp",
    libraryDependencies ++= Seq(
      Libraries.catsCore,
      Libraries.catsEffect,
      Libraries.catsLaws,
      Libraries.discipline,
      Libraries.disciplineScalatest,
      Libraries.scalatest

    )
  )


// Recursion Schemes

lazy val matryoshkaVersion = "0.21.3"
lazy val validationVersion = "2.1.0"

/*
resolvers ++= Seq(
  Resolver.sonatypeRepo("snapshots"),
  Resolver.sonatypeRepo("releases")
)*/


libraryDependencies ++= Seq(
  "com.slamdata" %% "matryoshka-core" % matryoshkaVersion,
  "com.slamdata" %% "matryoshka-scalacheck" % matryoshkaVersion,
  "org.apache.avro" % "avro" % "1.8.2",
  // JTO validation library
  "io.github.jto" %% "validation-core" % validationVersion,
  "io.github.jto" %% "validation-jsonast" % validationVersion,
  "org.scalactic" %% "scalactic" % "3.0.9",
  "org.scalatest" %% "scalatest" % "3.0.9" % "test"
)


parallelExecution in Test := false