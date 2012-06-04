package io.mth.foil

object FoilRunner {
  def run(foil: Foil) = {
    val jetty = Jetty.server(foil.port, foil.config)
    jetty.start
    jetty.join
  }
}