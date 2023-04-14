import sbt.Keys._
import sbt._

object Dependencies {

  object Versions {
    val scalacheckVersion = "1.17.0"
    val libphonenumberVersion = "8.13.9"
    val scalacheckGenRegexpVersion = "1.1.0"
    val scalacticVersion = "3.2.15"
    val scalatestVersion = "3.2.15"
    val scalatestplusScalacheckVersion = "3.2.15.0"
    val snappyVersion = "1.1.8.4"
  }

  import Dependencies.Versions._

  val scalacheck = "org.scalacheck" %% "scalacheck" % scalacheckVersion
  val libphonenumber = "com.googlecode.libphonenumber" % "libphonenumber" % libphonenumberVersion
  val scalacheckGenRegexp = "io.github.wolfendale" %% "scalacheck-gen-regexp" % scalacheckGenRegexpVersion
  val scalactic = "org.scalactic" %% "scalactic" % scalacticVersion % Test
  val scalatest = "org.scalatest" %% "scalatest" % scalatestVersion % Test
  val scalatestplusScalacheck = "org.scalatestplus" %% "scalacheck-1-17" % scalatestplusScalacheckVersion % Test
  val snappy = "org.xerial.snappy" % "snappy-java" % snappyVersion

  val metadata = libraryDependencies ++= Seq(
    scalacheck,
    scalactic,
    scalatest,
    scalatestplusScalacheck,
    snappy
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
