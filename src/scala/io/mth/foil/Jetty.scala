package io.mth.foil

import org.eclipse.jetty.server.handler._
import org.eclipse.jetty.server.Server
import org.eclipse.jetty.util.component._


object Jetty {
  type Port = Int

  def server(port: Port, config: JettyConfig): Server = {
    val server = new Server(port)
    server.setHandler(new ContextHandlerCollection)
    val binder = JettyBind.bind(config)
    binder(server)
    server.addLifeCycleListener(new LifeCycle.Listener() {
      def lifeCycleFailure(e: LifeCycle, c: Throwable) = {
        System.err.println("Server stopped unexpectedly: "
           c.getClass.getSimpleName + "[" + c.getMessage + "]" )
        c.printStackTrace
        sys.exit(1)
      }
      def lifeCycleStarted(e: LifeCycle) = {}
      def lifeCycleStarting(e: LifeCycle) = {}
      def lifeCycleStopped(e: LifeCycle) = {}
      def lifeCycleStopping(e: LifeCycle) = {}
    })
    server
  }
}
