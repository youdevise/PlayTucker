import sbt._
import sbt.Keys._
import play._
import play.PlayScala
import play.PlayImport._
import play.Play.autoImport._
import play.PlayImport.PlayKeys._

object PlayTuckerBuild extends Build {
  val playVersion = play.core.PlayVersion.current // see /project/play.sbt
  val tuckerVersion = "1.0.318"
  val metricsVersion = "3.0.2"

  // NOTE (2015-10-09, msiegel): Disable `fatal-warnings` until we rewrite deprecated Plugins as Modules
  lazy val compileOptions = scalacOptions ++= Seq("-deprecation", "-Ylog-classpath", "-unchecked", /* "-Xfatal-warnings", */ "-Xlint")

  lazy val commonLibs = Seq(
    "com.typesafe.play" %% "play"         % playVersion,
    "com.timgroup"      %  "Tucker"       % tuckerVersion intransitive(),
    "org.slf4j"         %  "slf4j-api"    % "[1.7.6]",
    "org.mockito"       %  "mockito-core" % "1.9.0" % "test",
    "org.scalactic"     %% "scalactic"    % "2.2.0",
    "org.scalatest"     %% "scalatest"    % "2.2.0" % "test"
  )

  val playTuckerCore = (project in file("modules/play-tucker-core/")).enablePlugins(PlayScala)
    .settings(compileOptions)
    .settings(libraryDependencies ++= commonLibs)

  val playMetricsGraphite = (project in file("modules/play-metrics-graphite/")).enablePlugins(PlayScala)
    .settings(compileOptions)
    .settings(libraryDependencies ++= commonLibs
                                    :+ "nl.grons"             %% "metrics-scala"    % "3.2.1"
                                    :+ "com.codahale.metrics" %  "metrics-core"     % metricsVersion
                                    :+ "com.codahale.metrics" %  "metrics-graphite" % metricsVersion
                                    :+ "com.codahale.metrics" %  "metrics-jvm"      % metricsVersion
                                    :+ "com.codahale.metrics" %  "metrics-servlet"  % metricsVersion
                                    :+ "com.codahale.metrics" %  "metrics-servlets" % metricsVersion)

  val playTuckerBoneCp = (project in file("modules/play-tucker-bonecp")).enablePlugins(PlayScala)
    .settings(compileOptions)
    .settings(libraryDependencies ++= commonLibs
                                    :+ jdbc
                                    :+ "mysql"              %  "mysql-connector-java" % "5.1.27"
             )
    .dependsOn(playTuckerCore)
    .dependsOn(playMetricsGraphite)

  val playTuckerJvmMetrics = (project in file("modules/play-tucker-jvmmetrics")).enablePlugins(PlayScala)
    .settings(compileOptions)
    .settings(libraryDependencies ++= commonLibs)
    .dependsOn(playTuckerCore)
    .dependsOn(playMetricsGraphite)

  val playTuckerSampleApp = (project in file(".")).enablePlugins(PlayScala)
    .settings(compileOptions)
    .settings(libraryDependencies += specs2 % Test)
    .dependsOn(playTuckerCore, playMetricsGraphite, playTuckerBoneCp, playTuckerJvmMetrics)
    .aggregate(playTuckerCore, playMetricsGraphite, playTuckerBoneCp, playTuckerJvmMetrics)
}