package io.mth.foil

import javax.servlet.Servlet
import org.eclipse.jetty.servlet.ServletContextHandler.NO_SESSIONS
import org.eclipse.jetty.servlet.{ServletHolder, ServletContextHandler}
import org.eclipse.jetty.webapp.WebAppContext
import org.eclipse.jetty.server.handler._
import org.eclipse.jetty.server.{Handler, Server}

/**
 * Responsible for binding combinations of jetty configs together. These
 * functions are responsible for ensuring that the jetty handlers are
 * constructed and added in a way which makes them composable. This is
 * achieved by ALWAYS using 'context' based handlers or wrappers and
 * maintaining a single ContextHandlerCollection as the root handler at all
 * times.
 */
/*
 * See [http://wiki.eclipse.org/Jetty/Tutorial/Embedding_Jetty] for
 * constructing handlers etc...
 */
object JettyBind {
  def bind(config: JettyConfig): Server => Unit = {

    def munge(server: Server, nu: Handler): Unit = {
      val handler = server.getHandler.asInstanceOf[ContextHandlerCollection]
      val old = if (handler.getHandlers != null) handler.getHandlers.toList else List[Handler]()
      val all = nu :: old
      handler.setHandlers(all.toArray)
      server.setHandler(handler)
    }

    def bindwar(context: String, war: String) = {
      (server: Server) =>
        val webapp = new WebAppContext()
        webapp.setContextPath(context)
        webapp.setWar(war)
        munge(server, webapp)
    }

    // FIX Is this really what we want, not sure how much value the default handler adds...
    def bindpath(context: String, path: String) = {
      (server: Server) =>
        val resource = new ResourceHandler()
        resource.setDirectoriesListed(true)
        resource.setWelcomeFiles(Array("index.html"))
        resource.setResourceBase(path)

        val default = new DefaultHandler()
        val handlers = new HandlerList()
        handlers.setHandlers(Array(resource, default))

        val c1 = new ContextHandler()
        c1.setContextPath(context)
        c1.setHandler(handlers)

        munge(server, c1)
    }

    def bindservlet(context: String, pattern: String, servlet: Servlet) = {
      (server: Server) =>
        val handler = new ServletContextHandler(NO_SESSIONS)
        handler.setContextPath(context)
        val holder = new ServletHolder(servlet)
        handler.addServlet(holder, pattern)

        munge(server, handler)
    }

    def bindcompound(configs: List[JettyConfig]) = {
      (server: Server) =>
        configs.foreach(bind(_)(server))
    }

    config.fold(
      c => w => bindwar(c, w),
      c => p => bindpath(c, p),
      c => p => s => bindservlet(c, p, s),
      cs => bindcompound(cs)
      )
  }


}