package io.mth.foil

import scala.collection.JavaConversions._
import javax.servlet.Servlet

trait JettyConfig {
  def fold[T](
    war: String => String => T,
    path: String => String => Boolean => T,
    servlet: String => String => Servlet => T,
    compound: List[JettyConfig] => T
  ): T
}


object JettyConfig extends JettyConfigs

trait JettyConfigs {
  def war(context: String, w: String): JettyConfig = new JettyConfig {
    def fold[T](
      war: String => String => T,
      path: String => String => Boolean => T,
      servlet: String => String => Servlet => T,
      compound: List[JettyConfig] => T
    ) = war(context)(w)
  }

  def path(context: String, p: String, cache: Boolean = true): JettyConfig = new JettyConfig {
    def fold[T](
      war: String => String => T,
      path: String => String => Boolean => T,
      servlet: String => String => Servlet => T,
      compound: List[JettyConfig] => T
    ) = path(context)(p)(cache)
  }

  def servlet(context: String, pattern: String, s: Servlet): JettyConfig = new JettyConfig {
    def fold[T](
      war: String => String => T,
      path: String => String => Boolean => T,
      servlet: String => String => Servlet => T,
      compound: List[JettyConfig] => T
    ) = servlet(context)(pattern)(s)
  }

  def compound(configs: List[JettyConfig]): JettyConfig = new JettyConfig {
    def fold[T](
      war: String => String => T,
      path: String => String => Boolean => T,
      servlet: String => String => Servlet => T,
      compound: List[JettyConfig] => T
    ) = compound(configs)
  }

}


