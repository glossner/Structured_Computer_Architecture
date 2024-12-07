ThisBuild / scalaVersion     := "2.13.12"
ThisBuild / version          := "0.1.0"
ThisBuild / organization     := "%ORGANIZATION%"

val chiselVersion = "6.2.0"

lazy val root = (project in file("."))
  .settings(
    name := "%NAME%",
    libraryDependencies ++= Seq(
      "org.chipsalliance" %% "chisel" % chiselVersion,
      "org.scalatest" %% "scalatest" % "3.2.16" % Test,
      "org.slf4j" % "slf4j-api" % "2.0.9",          // SLF4J API
      "org.slf4j" % "slf4j-simple" % "2.0.9"        // Simple SLF4J backend
    ),
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
      "-Ymacro-annotations"
    ),
    addCompilerPlugin("org.chipsalliance" % "chisel-plugin" % chiselVersion cross CrossVersion.full),
    
    // Enable SBT logging
    logLevel := Level.Debug, // Enable verbose logging
    
    // Enable logging by passing JVM properties to the application
    Compile / run / fork := true,
    Compile / run / javaOptions ++= Seq(
      "-Dorg.slf4j.simpleLogger.defaultLogLevel=WARN", // Set log level to WARN
      "-Dorg.slf4j.simpleLogger.showDateTime=true",    // Optional: Show timestamps
      "-Dorg.slf4j.simpleLogger.dateTimeFormat=yyyy-MM-dd HH:mm:ss" // Optional: Timestamp format
    )
  )
