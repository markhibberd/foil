package io.mth.foil.server


trait Slot {
  def foil: Foil

  def start()

  def stop()

  def status: Boolean
}
