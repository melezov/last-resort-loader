name := "last-resort-loader"

organization := "hr.element.lrl"

version := "0.0.1-SNAPSHOT"

scalaVersion := "2.9.2"

javacOptions  := Seq("-deprecation", "-Xlint:unchecked", "-encoding", "UTF-8", "-source", "1.6", "-target", "1.6")

autoScalaLibrary := false

crossPaths := false

unmanagedSourceDirectories in Compile <<= (javaSource in Compile)(_ :: Nil)

unmanagedSourceDirectories in Test := Nil

initialCommands := "import hr.element.lrl._"

resolvers := Seq(
  "Element Nexus" at "http://maven.element.hr/nexus/content/groups/public/"
)

externalResolvers <<= resolvers map ( rS =>
  Resolver.withDefaultResolvers(rS, mavenCentral = false)
)

libraryDependencies := Seq(
  "commons-io" % "commons-io" % "2.4"
, "commons-codec" % "commons-codec" % "1.6"
)

publishTo <<= version( version => Some(
  if (version endsWith "SNAPSHOT") {
    "Element Snapshots" at "http://maven.element.hr/nexus/content/repositories/snapshots/"
  }
  else {
    "Element Releases"  at "http://maven.element.hr/nexus/content/repositories/releases/"
  }
))

credentials += Credentials(Path.userHome / ".publish" / "element.credentials")

publishArtifact in (Compile, packageDoc) := false

EclipseKeys.projectFlavor := EclipseProjectFlavor.Java
