package io.mth.foil.server

import org.eclipse.jetty.server.handler._
import org.eclipse.jetty.server.Server


object Jetty {
  type Port = Int

  def server(port: Port, config: JettyConfig): Server = {
    val server = new Server(port)
    server.setHandler(new ContextHandlerCollection)
    val binder = JettyBind.bind(config)
    binder(server)
    server
  }
}
