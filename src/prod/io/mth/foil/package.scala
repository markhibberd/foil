package io.mth

package object foil extends JettyConfigs {
  def runfoil(foil: Foil) = FoilRunner.run(foil)
}
