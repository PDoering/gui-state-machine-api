name := "gui-state-machine-api"

organization := "de.retest"

scalaVersion := "2.11.8"

/*
 * retest-sut-api provides a package and an object with the name a etc.
 * We have to resolve the conflict.
 */
scalacOptions := Seq("-unchecked", "-deprecation", "-feature", "-Yresolve-term-conflict:package")

// Disable using the Scala version in output paths and artifacts:
crossPaths := false

// Fixes serialization issues:
fork := true

resolvers += "nexus-retest-maven-all" at "https://nexus.retest.org/repository/all/"

resolvers += "sonatype-snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

// Dependencies for a graph database:
libraryDependencies += "org.neo4j" % "neo4j" % "3.5.4"
libraryDependencies += "org.neo4j" % "neo4j-ogm-core" % "3.1.8"
libraryDependencies += "org.neo4j" % "neo4j-ogm-embedded-driver" % "3.1.8"
libraryDependencies += "org.neo4j" % "neo4j-bolt" % "3.5.4"

// Dependencies to represent states and actions:
libraryDependencies += "de.retest" % "surili-commons" % "0.1.0-SNAPSHOT" withSources () withJavadoc () changing ()
libraryDependencies += "javax.xml.bind" % "jaxb-api" % "2.3.0"

// Dependencies to write GML files for yEd:
libraryDependencies += "com.github.systemdir.gml" % "GMLWriterForYed" % "2.1.0"
libraryDependencies += "org.jgrapht" % "jgrapht-core" % "1.0.1"

// Logging:
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0"

// Test frameworks:
libraryDependencies += "org.scalatest" %% "scalatest" % "3.0.5" % "test"

// format the code
scalafmtOnCompile := true

// ReTest's Nexus:
publishTo := {
  val nexus = "https://nexus.retest.org/repository/"
  if (isSnapshot.value) {
    Some("snapshots" at nexus + "surili-snapshot")
  } else {
    Some("releases" at nexus + "surili")
  }
}

sys.env.get("TRAVIS_NEXUS_PW") match {
  case Some(password) =>
    credentials += Credentials("ReTest Nexus", "nexus.retest.org", "retest", password)
  case _ =>
    throw new IllegalStateException("TRAVIS_NEXUS_PW is not defined as environment variable!")
}
