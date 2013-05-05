name := "jess"

version := "0.1.0"

scalaVersion := "2.10.1"

scalacOptions ++= Seq("-deprecation")

resolvers ++= Seq(
  "Typesafe Releases Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies += "play" % "play_2.10" % "2.1.1"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0.M5b" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % "test"
