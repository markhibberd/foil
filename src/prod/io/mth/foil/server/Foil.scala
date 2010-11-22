package io.mth.foil.server

import java.util.UUID


case class Foil(uuid: String, port: Int, config: JettyConfig)

object Foil {
  def apply(port: Int, config: JettyConfig): Foil = Foil(UUID.randomUUID().toString, port, config)
}



