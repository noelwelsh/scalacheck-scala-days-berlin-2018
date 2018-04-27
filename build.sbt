enablePlugins(TutPlugin)

scalaVersion := "2.12.4"

libraryDependencies ++=
  "org.scalatest"  %% "scalatest"  % "3.0.5" ::
  "org.scalacheck" %% "scalacheck" % "1.14.0" ::
  Nil

scalacOptions ++= Seq(
  "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
  "-encoding", "utf-8",                // Specify character encoding used by source files.
  "-explaintypes",                     // Explain type errors in more detail.
  "-feature"
)