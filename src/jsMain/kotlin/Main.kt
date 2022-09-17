
import net.dinkla.raytracer.examples.definitions
import net.dinkla.raytracer.gui.outputPngFileName
import net.dinkla.raytracer.renderer.Renderers
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.world.Context
import net.dinkla.raytracer.world.Render

suspend fun main() {
    val args = listOf<String>("World20.kt")
    Logger.info("From-the-ground-up-raytracer on node/JavaScript")
    if (args.size != 1) {
        Logger.error("CommandLineUI expects world definition filename as input")
        Logger.info("Possible worlds are ${definitions.keys.joinToString(",")}")
        return
    }
    val context = Context(Tracers.WHITTED.create, Renderers.SEQUENTIAL.create, Resolution.RESOLUTION_480)
    Render.render(args[0], outputPngFileName(args[0]), context)

    // wait?
    var sum = 1.0
    for (i in (0..1_000_000_000)) {
        sum *= kotlin.math.acos(sum) * kotlin.math.acosh(i.toDouble())
    }
    Logger.info("sum $sum")
}
