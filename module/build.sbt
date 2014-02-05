name := "play-bonecp-tucker"

organization := "com.timgroup"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.9.1"

playVersion := "2.0.8"

tuckerVersion := "1.0.272"

crossScalaVersions := Seq("2.9.1", "2.9.2", "2.9.3")

resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases/"

resolvers += "repo-public" at "http://repo.youdevise.com:8081/nexus/content/groups/public"

libraryDependencies ++= Seq(
  "org.slf4j" % "slf4j-api" % "[1.6.6]",
  // Test-only dependencies
  "org.mockito" % "mockito-core" % "1.9.0" % "test"
)

libraryDependencies <++= (scalaVersion, playVersion) { CrossVersionDependencies.play(_, _) }

libraryDependencies <++= (scalaVersion) { CrossVersionDependencies.scalatest(_) }

libraryDependencies <++= (tuckerVersion) { version => Seq("com.timgroup" % "Tucker" % version intransitive())  }