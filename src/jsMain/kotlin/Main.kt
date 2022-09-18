
import net.dinkla.raytracer.gui.outputPngFileName
import net.dinkla.raytracer.renderer.Renderer
import net.dinkla.raytracer.renderer.createRenderer
import net.dinkla.raytracer.synopsis
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.world.Context
import net.dinkla.raytracer.world.Render
import node.process.process

suspend fun main() {
    val args = process.argv.toList().drop(2)
    Logger.info("From-the-ground-up-raytracer on node/JavaScript")
    Logger.info("Called with args '$args'")
    if (args.size != 1) {
        synopsis("CommandLineUI")
        return
    }
    val context = Context(Tracers.WHITTED.create, createRenderer(Renderer.SEQUENTIAL), Resolution.RESOLUTION_480)
    Render.render(args[0], outputPngFileName(args[0]), context)

    // wait?
    var sum = 1.0
    for (i in (0..1_000_000_000)) {
        sum *= kotlin.math.acos(sum) * kotlin.math.acosh(i.toDouble())
    }
    Logger.info("sum $sum")
}
