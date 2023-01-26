inThisBuild(
  List(
    organization := "io.github.mr-tolmach",
    homepage := Some(url("https://github.com/mr-tolmach/raf")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository :="https://s01.oss.sonatype.org/service/local",
    scalaVersion := Versions.defaultScalaVersion,
    crossScalaVersions := Seq("2.12.17", Versions.defaultScalaVersion),
    Test / logBuffered := false
  )
)

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots",
  "guava" at "https://mvnrepository.com/artifact/com.google.guava/guava"
)

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
    publish / skip := true,
    publishArtifact := false
  ).aggregate(metadata, generators)
