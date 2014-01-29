package com.ee.midas.utils

import scala.tools.nsc.{Settings, Global}
import scala.tools.nsc.reporters.ConsoleReporter

trait ScalaCompiler extends Loggable {
  def compile(classpathDir: String, outputDir: String, file: String): Unit =
    compile(classpathDir, outputDir, List(file))

  def compile(classpathDir: String, outputDir: String, files: List[String]): Unit = {
    def error(message: String): Unit = {
      logError(s"Scala Compiler Error $message")
    }
    val runtimeClasspath = System.getProperty("java.class.path")
    logDebug(s"Runtime Classpath = $runtimeClasspath")
    logDebug(s"Inside Compile classpathDir = $classpathDir, outputDirURI = $outputDir, files to compile = $files")
    val settings = new Settings(error)

    settings.outdir.value = outputDir
    settings.processArgumentString("-usejavacp")
    settings.classpath.value = classpathDir

    val reporter = new ConsoleReporter(settings)
    val compiler = new Global(settings, reporter)
    logInfo(s"Started Compilation of $files")
    val run = new compiler.Run
    run compile files
    logInfo(s"Completed Compilation of $files")
//    reporter.printSummary()
    if(reporter.hasErrors) {
      logError(s"Compilation Failed!")
      throw new RuntimeException(s"Compilation Failed!")
    }
  }
}