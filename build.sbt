val defaultScalaVersion = "2.13.10"

inThisBuild(
  List(
    organization := "io.github.mr-tolmach",
    homepage := Some(url("https://github.com/mr-tolmach/raf")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    scalaVersion := defaultScalaVersion,
    crossScalaVersions := Seq("2.12.17", defaultScalaVersion),
    publish / skip := true,
    Test / logBuffered := false
  )
)

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

lazy val metadata = (project in file("metadata"))
  .settings(
    name := "metadata",
    publish / skip := false,
    version := Versions.generators,
    Dependencies.metadata
  )

lazy val generators = (project in file("generators"))
  .settings(
    name := "generators",
    publish / skip := false,
    version := Versions.generators,
    Dependencies.generators
  ).dependsOn(metadata)

lazy val root = (project in file("."))
  .settings(
    name := "raf",
    publishArtifact := false
  ).aggregate(metadata, generators)
