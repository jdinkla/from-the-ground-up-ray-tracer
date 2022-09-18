
import net.dinkla.raytracer.gui.outputPngFileName
import net.dinkla.raytracer.renderer.Renderers
import net.dinkla.raytracer.synopsis
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.world.Context
import net.dinkla.raytracer.world.Render

suspend fun main(args: Array<String>) {
    Logger.info("From-the-ground-up-raytracer on node/JavaScript")
    if (args.size != 1) {
        synopsis("CommandLineUI")
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
