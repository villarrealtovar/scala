import sbt._

object Dependencies {

  object Version {
    val cats = "2.1.1"
    val discipline = "1.5.1"
    val disciplineScalatest = "2.2.0"
    val scalatest = "3.2.3"
  }

  object Libraries {
    def cats(artifact: String): ModuleID = "org.typelevel" %% artifact % Version.cats

    val catsCore = cats("cats-core")
    val catsEffet = cats("cats-effect")
    val catsLaws = cats("cats-laws")
    val discipline = "org.typelevel" %% "discipline-core" % Version.discipline
    val disciplineScalatest = "org.typelevel" %% "discipline-scalatest" %  Version.disciplineScalatest
    val scalatest = "org.scalatest" %% "scalatest" % Version.scalatest


  }
}