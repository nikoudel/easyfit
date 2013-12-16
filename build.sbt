name := "easyfit"

version := "0.1"

resolvers += "Sonatype OSS Public" at "https://oss.sonatype.org/content/groups/public/"

libraryDependencies += "org.scalatest" %% "scalatest" % "2.0" % "test"

scalacOptions ++= Seq("-unchecked", "-deprecation")

unmanagedSourceDirectories in Compile += baseDirectory.value / "minimal-json/com.eclipsesource.json/src/main"