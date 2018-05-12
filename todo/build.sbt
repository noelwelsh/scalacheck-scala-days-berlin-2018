val Http4sVersion = "0.18.11"
val Specs2Version = "4.1.0"
val LogbackVersion = "1.2.3"

lazy val todo = (project in file("."))
  .settings(
    organization := "io.underscore.testing",
    name := "todo",
    version := "0.0.1-SNAPSHOT",
    scalaVersion := "2.12.6",
    libraryDependencies ++= Seq(
      "io.circe"        %% "circe-generic"       % "0.9.3",
      "io.circe"        %% "circe-java8"         % "0.9.3",
      "org.http4s"      %% "http4s-blaze-server" % Http4sVersion,
      "org.http4s"      %% "http4s-circe"        % Http4sVersion,
      "org.http4s"      %% "http4s-dsl"          % Http4sVersion,
      "org.scalacheck"  %% "scalacheck"          % "1.14.0",
      "ch.qos.logback"  %  "logback-classic"     % LogbackVersion
    ),
    scalacOptions ++= Seq(
      "-deprecation",                      // Emit warning and location for usages of deprecated APIs.
      "-encoding", "utf-8",                // Specify character encoding used by source files.
      "-explaintypes",                     // Explain type errors in more detail.
      "-feature",
      "-language:higherKinds"
    )
  )

