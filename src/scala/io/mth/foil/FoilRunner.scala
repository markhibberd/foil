package io.mth.foil

object FoilRunner {
  def run(foil: Foil) = {
    val jetty = Jetty.server(foil.port, foil.config)
    jetty.start
    jetty.join
  }

  def rundev(foil: Foil) = {
    val jetty = Jetty.server(foil.port, foil.config)
    jetty.start
    println("Server started, hit enter to exit...")
    readLine
    sys.exit(0)
  }
}
