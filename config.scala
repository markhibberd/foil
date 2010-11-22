import io.mth.foil.fabric.servlet.InteractiveFoil
import io.mth.foil.server._
import java.io.File

val c1 = JettyConfig.servlet("/a", "/*", new InteractiveFoil)
val c2 = JettyConfig.path("/b", ".")
val c3 = JettyConfig.path("/", "/home/mth")
val compound = JettyConfig.compound(List(c1, c3, c2))

slots.register(Foil(10081, c1), true)
slots.register(Foil(10082, c2), true)
slots.register(Foil(10080, compound), true)
