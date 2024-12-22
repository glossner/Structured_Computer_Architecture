ThisBuild / scalaVersion     := "2.13.15"
ThisBuild / version          := "0.1.0"
ThisBuild / organization     := "%ORGANIZATION%"

val chiselVersion = "6.6.0"

lazy val root = (project in file("."))
  .settings(
    name := "%NAME%",
    libraryDependencies ++= Seq(
      "org.chipsalliance" %% "chisel" % chiselVersion,
      "org.scalatest" %% "scalatest" % "3.2.16" % Test,
      //"ch.epfl.scala" % "sbt-bloop_2.12_1.0" % "2.0.5",  //bloop in VSC
      "org.slf4j" % "slf4j-api" % "2.0.9",          // SLF4J API
      "org.slf4j" % "slf4j-simple" % "2.0.9"        // Simple SLF4J backend
    ),

    // It seems sbt-bloop is not compatible with scala 2.13 yet
    // https://index.scala-lang.org/scalacenter/bloop/artifacts/sbt-bloop
    //addSbtPlugin("ch.epfl.scala" % "sbt-bloop-core" % "1.4.1"),
 
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
      "-Ymacro-annotations"
    ),

    
    addCompilerPlugin("org.chipsalliance" % "chisel-plugin" % chiselVersion cross CrossVersion.full),

    // Add custom source directory for generators
    Compile / unmanagedSourceDirectories += baseDirectory.value / "generators",
 
    // Enable SBT logging
    //logLevel := Level.Debug, // Enable verbose logging
    
    // Enable logging by passing JVM properties to the application
    Compile / run / fork := true,
    Compile / run / javaOptions ++= Seq(
      "-Xmx4G",
      "-Dchisel.firtool=true", // Ensures CIRCT is the backend. https://github.com/llvm/circt 
      "-Dorg.slf4j.simpleLogger.defaultLogLevel=WARN", // Set log level to WARN
      "-Dorg.slf4j.simpleLogger.showDateTime=true",    // Optional: Show timestamps
      "-Dorg.slf4j.simpleLogger.dateTimeFormat=yyyy-MM-dd HH:mm:ss" // Optional: Timestamp format
    ),

    // Compile / runMain / javaOptions ++= Seq(
    //   "-Xmx4G",
    //   "-Dchisel.firtool=true",
    //   "-Dorg.slf4j.simpleLogger.defaultLogLevel=WARN", 
    //   "-Dorg.slf4j.simpleLogger.showDateTime=true",    
    //   "-Dorg.slf4j.simpleLogger.dateTimeFormat=yyyy-MM-dd HH:mm:ss" 
    // )
  )
