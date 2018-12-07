name := "sensormultiplexer"

version := "0.1.2"

scalaVersion := "2.11.11"

libraryDependencies += "org.apache.kafka" %% "kafka" % "0.10.2.2" exclude("javax.jms", "jms")
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.4.0" % Test
)
libraryDependencies += "com.github.jodersky" %% "flow" % "2.3.0"
libraryDependencies += "com.github.jodersky" % "flow-native" % "2.3.0"
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.0" % "test"

val circeVersion = "0.10.1"

libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)