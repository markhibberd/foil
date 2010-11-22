package io.mth.foil.fabric.servlet

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

class InteractiveFoil extends HttpServlet {
  override def service(req: HttpServletRequest, resp: HttpServletResponse) = {
    val out = resp.getWriter
    out.println("content-type: " + req.getContentType)
    out.println("context-path: " + req.getContextPath)
    out.println("request-meth: " + req.getMethod)
  }
}