name := "jess"

version := "0.1.0"

scalaVersion := "2.10.4"

scalacOptions ++= Seq("-deprecation")

resolvers ++= Seq(
  "Typesafe Releases Repository" at "http://repo.typesafe.com/typesafe/releases/"
)

libraryDependencies += "org.scalaz" %% "scalaz-core" % "7.1.0"

libraryDependencies += "com.typesafe.play" %% "play-json" % "2.2.1"

libraryDependencies += "org.scalatest" % "scalatest_2.10" % "2.0" % "test"

libraryDependencies += "junit" % "junit" % "4.10" % "test"

initialCommands += "import scalaz._, scalaz.Scalaz._"
