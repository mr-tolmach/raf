import sbt.Keys._
import sbt._

object Dependencies {

  object Versions {
    val scalacheckVersion = "1.16.0"
    val libphonenumberVersion = "8.13.0"
    val scalacheckGenRegexpVersion = "0.1.3"
    val scalacticVersion = "3.2.12"
    val scalatestVersion = "3.2.12"
    val scalatestplusScalacheckVersion = "3.2.12.0"
    val snappyVersion = "1.1.8.4"
    val guavaVersion = "31.1-jre"
  }

  import Dependencies.Versions._

  val scalacheck = "org.scalacheck" %% "scalacheck" % scalacheckVersion
  val libphonenumber = "com.googlecode.libphonenumber" % "libphonenumber" % libphonenumberVersion
  val scalacheckGenRegexp = "io.github.wolfendale" %% "scalacheck-gen-regexp" % scalacheckGenRegexpVersion
  val scalactic = "org.scalactic" %% "scalactic" % scalacticVersion % Test
  val scalatest = "org.scalatest" %% "scalatest" % scalatestVersion % Test
  val scalatestplusScalacheck = "org.scalatestplus" %% "scalacheck-1-16" % scalatestplusScalacheckVersion % Test
  val snappy = "org.xerial.snappy" % "snappy-java" % snappyVersion
  val guava = "com.google.guava" % "guava" % guavaVersion

  val metadata = libraryDependencies ++= Seq(
    scalacheck,
    scalactic,
    scalatest,
    scalatestplusScalacheck,
    snappy,
    guava
  )

  val generators = libraryDependencies ++= Seq(
    scalacheck,
    libphonenumber % Test,
    scalacheckGenRegexp,
    scalactic,
    scalatest,
    scalatestplusScalacheck,
    snappy
  )

}
