package io.mth.foil

case class Foil(uuid: String, port: Int, config: JettyConfig) {
  def this(port: Int, config: JettyConfig) = this(java.util.UUID.randomUUID().toString, port, config)
}

object Foil {
  def apply(port: Int, config: JettyConfig): Foil = new Foil(port, config)
}
