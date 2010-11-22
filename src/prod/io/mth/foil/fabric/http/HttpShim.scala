package io.mth.foil.fabric.http

import javax.servlet.http.{HttpServletResponse, HttpServletRequest, HttpServlet}

class HttpShim extends HttpServlet {
  override def service(req: HttpServletRequest, resp: HttpServletResponse) = {

  }
}