package io.mth.foil.j

import java.lang.String
import javax.servlet.Servlet
import io.mth.foil.JettyConfig


class DefaultConfigs extends Configs {
  def config(j: JettyConfig): Config = new Config {
    val jetty = j
  }

  def compound(configs:Config*) = config(JettyConfig.compound(configs.toList map (_.jetty)))

  def servlet(context: String, pattern: String, s: Servlet) = config(JettyConfig.servlet(context, pattern, s))

  def path(context: String, path: String) = config(JettyConfig.path(context, path))

  def war(context: String, war: String) = config(JettyConfig.war(context, war))
}
