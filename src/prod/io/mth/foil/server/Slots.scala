package io.mth.foil.server

trait Slots {
  def status(pred: Foil => Boolean): List[Boolean]

  def status(uuid: String): Boolean = status((foil: Foil) => foil.uuid == uuid).head

  def status(foil: Foil): Boolean = status(foil.uuid)

  def status(): List[Boolean] = status((foil: Foil) => true)

  def start(pred: Foil => Boolean): Unit

  def start(uuid: String): Unit = start((foil: Foil) => foil.uuid == uuid)

  def start(foil: Foil): Unit = start(foil.uuid)

  def start(): Unit = start((foil: Foil) => true)

  def stop(pred: Foil => Boolean): Unit

  def stop(uuid: String): Unit = stop((foil: Foil) => foil.uuid == uuid)

  def stop(foil: Foil): Unit = stop(foil.uuid)

  def stop(): Unit = stop((foil: Foil) => true): Unit

  def update(pred: Foil => Boolean)(f: Foil => Foil): Unit

  def update(uuid: String)(f: Foil => Foil): Unit = update((foil: Foil) => foil.uuid == uuid)(f)

  def update(f: Foil => Foil): Unit = update((foil: Foil) => true)(f)

  def register(foil: Foil, start: Boolean): Unit
}

object Slots {
  def apply() = new Slots {
    var guts: List[Slot] = List()

    def start(pred: Foil => Boolean) = (guts filter (s => pred(s.foil))) foreach (_.start())

    def stop(pred: Foil => Boolean) = (guts filter (s => pred(s.foil))) foreach (_.stop())

    def status(pred: Foil => Boolean) = (guts filter (s => pred(s.foil))) map (_.status)

    def update(pred: Foil => Boolean)(f: Foil => Foil): Unit  = {

    }

    def register(f: Foil, start: Boolean) = {
      if (guts.exists(_.foil.uuid == f.uuid))
        throw new IllegalArgumentException("Duplicate UUID [" + f.uuid + "].")
      val jetty = Jetty.server(f.port, f.config)
      val slot = new Slot {
        def foil = f
        def start = jetty.start()
        def stop = jetty.stop()
        def status = jetty.isRunning
      }
      if (start)
        slot.start
      guts = slot :: guts
    }
  }
}
