enablePlugins(ScalaJSPlugin)
// Use Node.
scalaJSUseRhino in Global := false

lazy val autowireElectron = (project in file(".")).
  settings(
    organization := "com.mscharley",
    name := "autowire-electron",
    version := "0.2.0-SNAPSHOT",
    scalaVersion := "2.11.8",

    // Info for Sonatype.
    publishMavenStyle := true,
    publishArtifact in Test := false,
    pomIncludeRepository := { _ => false },
    licenses := Seq("MIT" -> url("https://github.com/mscharley/autowire-electron/blob/master/LICENSE.md")),
    homepage := Some(url("https://github.com/mscharley/autowire-electron")),
    pomExtra := (
  <scm>
    <url>git@github.com:mscharley/autowire-electron.git</url>
    <connection>scm:git:git@github.com:mscharley/autowire-electron.git</connection>
  </scm>
  <developers>
    <developer>
      <id>mscharley</id>
      <name>Matthew Scharley</name>
      <url>http://matt.scharley.me/</url>
    </developer>
  </developers>),
    libraryDependencies ++= Seq(
      "com.lihaoyi"   %%% "autowire"         % "0.2.5",
      "com.mscharley" %%% "scalajs-electron" % "0.1.2"
    )
  )
