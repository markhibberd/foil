import io.mth.foil._

import javax.servlet.http._

class DemoServlet extends HttpServlet {
  override def service(req: HttpServletRequest, resp: HttpServletResponse) = {
    val path = req.getPathInfo
    val writer = resp.getWriter
    writer.println("hello from " + path)
  }
}

val config = compound(List(
  servlet("/", "/*", new DemoServlet),    // bind DemoServlet to / context, matching all urls
  path("/static", "static")               // bind directory resources to /static url.
))

val foil = Foil("demp", 10080, config)    // create server
runfoil(foil)                             // run server

