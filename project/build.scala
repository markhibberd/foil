import sbt._
import Keys._

object build extends Build {
  type Sett = Project.Setting[_]

  override lazy val settings = super.settings ++
        Seq(resolvers := Seq(
          "mth.io snapshots" at "http://repo.mth.io/snapshots"
        , "mth.io releases" at "http://repo.mth.io/releases"
        , "snapshots" at "http://oss.sonatype.org/content/repositories/snapshots"
        , "releases" at "http://oss.sonatype.org/content/repositories/releases"
        ))

  lazy val publishSetting = publishTo <<= (version).apply{
    v => {
      val flavour = if (v.trim.endsWith("SNAPSHOT")) "snapshots" else "releases"
      Some(Resolver.sftp("repo.mth.io","repo.mth.io", "repo.mth.io/data/snapshots") as ("web", new java.io.File("/home/mth/.ssh/id_dsa")))
    }
  }

  val foil = Project(
    id = "foil"
  , base = file(".")
  , settings = Defaults.defaultSettings ++ Seq[Sett](
      name := "foil"
    , organization := "io.mth"
    , version := "1.3-SNAPSHOT"
    , scalaVersion := "2.9.2"
    , scalacOptions := Seq(
        "-deprecation"
      , "-unchecked"
      )
    , sourceDirectory in Compile <<= baseDirectory { _ / "src" }
    , sourceDirectory in Test <<= baseDirectory { _ / "test" }
    , historyPath <<= baseDirectory { b => Some(b / "gen/sbt/.history") }
    , target <<= baseDirectory { _ / "gen/sbt/target" }
    , testOptions in Test += Tests.Setup(() => System.setProperty("specs2.outDir", "gen/sbt/target/specs2-reports"))
    , publishSetting
    , libraryDependencies ++= Seq(
        "org.specs2" % "specs2_2.9.2" % "1.9" % "test" withSources
      , "org.scalacheck" %% "scalacheck" % "1.9" % "test" withSources
      )
    )
  )
}
