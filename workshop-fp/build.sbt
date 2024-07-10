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
      Libraries.catsEffet,
      Libraries.catsLaws,
      Libraries.discipline,
      Libraries.disciplineScalatest,
      Libraries.scalatest

    )
  )