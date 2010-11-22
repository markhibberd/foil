package io.mth.foil.launch.standalone

import tools.nsc.{GenericRunnerSettings, Settings}
import io.mth.foil.server.{Slot, Slots}
import java.net.URLClassLoader
import tools.nsc.io.File


object Main {
  def usage = """
      |  foil [-h] [-e expression] [-c] [-v] [<config.scala>]
      |
      |  Options:
      |   -e, --eval <expression>        evalute expression instead of dropping
      |                                  into console.
      |   -c, --console                  drop into console after startup (this
      |                                  is the default if no config specified)
      |   -h, --help                     display this usage message
      |   -v, --version                  display version information
      |
      |  Arguments:
      |   <config.scala>                 config file, in scala. commands run as
      |                                  per console. Config is optional, no
      |                                  config is equivalent to `--console`.
      |
  """.stripMargin

  // FIX read from manifest...
  def version = "foil development version 0"

  def main(args: Array[String]) = {
    // FIX some awesomely inefficient command line parsing... or non-parsing as it would be. replace with pirate


    def mungeToPositional(l: List[String]): List[String] = {
      var acc = l;
      while (acc.size > 0) {
        val x = acc(0)
        val toDrop = if (x == "-e" || x == "--eval") 2
                     else if (x.startsWith("-")) 1
                     else return acc
        acc.drop(toDrop)
      }
      List[String]()
    }


    val splitter = args.indexOf("--")
    val trunced = if (splitter > 0) args.slice(0, splitter) else args
    val positional = if (splitter > 0) args.slice(splitter + 1, args.length)
                     else mungeToPositional(args.toList).toArray


    def hasFlag(short: String, long: String) = trunced.exists(s => s == short || s == long)
    def positionOfFlag(short: String, long: String) = trunced.indexWhere(s => s == short || s == long)

    if (hasFlag("-h", "--help")) {
      println(usage)
      System.exit(0)
    }

    if (hasFlag("-v", "--version")) {
      println(version)
      System.exit(0)
    }

    val eval: Option[String] = if (hasFlag("-e", "--eval")) {
      val idx = positionOfFlag("-e", "--eval")
      if (trunced.length < idx + 1) {
        System.err.println("--eval expects argument")
        System.err.println(usage)
        System.exit(1)
      }
      Some(trunced(idx + 1))
    } else None

    val console = hasFlag("-c", "--console")

    val slots = Slots()

    val settings = new GenericRunnerSettings(Console.println _)

    val cls = classOf[Slot]
    val loader: URLClassLoader = cls.getClassLoader.asInstanceOf[URLClassLoader]
    val paths = loader.getURLs.map(x => x.getFile)
    paths.foreach(settings.classpath.append _)

    val x = new scala.tools.nsc.InterpreterLoop(None, new java.io.PrintWriter(System.out)) {
      override def createInterpreter() {
        super.createInterpreter()
        interpreter.beQuietDuring {
          interpreter.bind("slots", "io.mth.foil.server.Slots", slots)
        }
        interpretAllFrom(File(new java.io.File("config.scala")))
      }
    }

    x.main(settings)

    slots.stop()
  }
}
