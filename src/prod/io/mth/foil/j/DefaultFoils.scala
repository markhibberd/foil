package io.mth.foil.j

import java.lang.String
import io.mth.foil.{Foil => F, FoilRunner}

class DefaultFoils extends Foils{
  def nu(name: String, port: Int, config: Config): Foil = new Foil {
    def run = {
      FoilRunner.run(F(name, port, config.jetty))
    }
  }
}