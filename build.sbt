import scala.xml.{Node => XmlNode, NodeSeq => XmlNodeSeq, _}
import scala.xml.transform.{RewriteRule, RuleTransformer}

val defaultScalaVersion = "2.13.10"

inThisBuild(
  List(
    organization := "io.github.mr-tolmach",
    homepage := Some(url("https://github.com/mr-tolmach/raf")),
    licenses := List("Apache-2.0" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
    sonatypeCredentialHost := "s01.oss.sonatype.org",
    sonatypeRepository := "https://s01.oss.sonatype.org/service/local",
    versionScheme := Some("early-semver"),
    developers := List(
      Developer(
        "mr-tolmach",
        "Daniil Tolmachev",
        "mister.tolmach@gmail.com",
        url("https://github.com/mr-tolmach")
      )
    ),
    scalaVersion := defaultScalaVersion,
    crossScalaVersions := Seq("2.12.17", defaultScalaVersion, "3.2.2"),
    Test / logBuffered := false,
    // skip dependency elements with a test scope
    pomPostProcess := { (node: XmlNode) =>
      new RuleTransformer(new RewriteRule {

        private def isTestScoped(e: Elem): Boolean =
          e.label == "dependency" && e.child.exists(child => child.label == "scope" && child.text == "test")

        override def transform(node: XmlNode): XmlNodeSeq = node match {
          case e: Elem if isTestScoped(e) =>
            val dependencyLine = Seq("groupId", "artifactId", "version", "scope")
              .map { label =>
                e.child.filter(_.label == label).flatMap(_.text).mkString("\"", "", "\"")
              }
              .mkString(" % ")

            sLog.value.info(s"""Dependency $dependencyLine has been omitted""")

            XmlNodeSeq.Empty
          case _ => node
        }
      }).transform(node).head
    }
  )
)

resolvers ++= Seq(
  "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
)

lazy val metadata = (project in file("metadata"))
  .settings(
    moduleName := "raf-metadata",
    publish / skip := false,
    Dependencies.metadata
  )

lazy val generators = (project in file("generators"))
  .settings(
    moduleName := "raf-generators",
    publish / skip := false,
    Dependencies.generators
  )
  .dependsOn(metadata)

lazy val root = (project in file("."))
  .settings(
    name := "raf",
    publish / skip := true,
    publishArtifact := false
  )
  .aggregate(metadata, generators)
