
import kotlinx.coroutines.runBlocking
import net.dinkla.raytracer.gui.outputPngFileName
import net.dinkla.raytracer.renderer.Renderers
import net.dinkla.raytracer.synopsis
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.world.Context
import net.dinkla.raytracer.world.Render

fun main(args: Array<String>) = runBlocking {
    Logger.info("From-the-ground-up-raytracer on JVM")
    if (args.size != 1) {
        synopsis("CommandLineUI")
        return@runBlocking
    }
    val context = Context(Tracers.WHITTED.create, Renderers.SEQUENTIAL.create, Resolution.RESOLUTION_1080)
    Render.render(args[0], outputPngFileName(args[0]), context)
}
