
import net.dinkla.raytracer.renderer.Renderers
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.world.Context
import net.dinkla.raytracer.world.Render

suspend fun main() {
    Logger.info("From-the-ground-up-raytracer on node/JavaScript")
    val args = listOf("World10.kt", "out-js1.png")
    if (args.size != 2) {
        Logger.error("CommandLineUI expects input filename and output filename as arguments")
        return
    }
    val context = Context(Tracers.WHITTED.create, Renderers.SEQUENTIAL.create, Resolution.RESOLUTION_480)
    Render.render(args[0], args[1], context)

    // wait?
    var sum = 1.0
    for (i in (0..1_000_000_000)) {
        sum *= kotlin.math.acos(sum) * kotlin.math.acosh(i.toDouble())
    }
    Logger.info("sum $sum")
}
