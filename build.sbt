name := "easyfit"

version := "0.1"

resolvers += "Sonatype OSS Public" at "https://oss.sonatype.org/content/groups/public/"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.0" % "test"

libraryDependencies += "org.apache.logging.log4j" % "log4j-api" % "2.0-beta9"

libraryDependencies += "org.apache.logging.log4j" % "log4j-core" % "2.0-beta9"

scalacOptions ++= Seq("-unchecked", "-deprecation")

unmanagedSourceDirectories in Compile += baseDirectory.value / "minimal-json/com.eclipsesource.json/src/main"