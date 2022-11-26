ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.10"

Test / logBuffered := false

resolvers +=
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"

lazy val metadata = (project in file("metadata"))
  .settings(
    name := "metadata",
    publishArtifact := false,
    Dependencies.metadata
  )

lazy val generators = (project in file("generators"))
  .settings(
    name := "generators",
    publishMavenStyle := true,
    Dependencies.generators
  ).dependsOn(metadata)

lazy val root = (project in file("."))
  .settings(
    name := "raf",
    publishArtifact := false
  ).aggregate(metadata, generators)
