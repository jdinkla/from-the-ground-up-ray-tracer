
import kotlinx.coroutines.runBlocking
import net.dinkla.raytracer.examples.definitions
import net.dinkla.raytracer.gui.outputPngFileName
import net.dinkla.raytracer.renderer.Renderers
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.world.Context
import net.dinkla.raytracer.world.Render

fun main() = runBlocking {
    Logger.info("From-the-ground-up-raytracer on JVM")
    val args = listOf<String>("World20.kt")
    if (args.size != 1) {
        Logger.error("CommandLineUI expects input filename and output filename as arguments")
        Logger.info("Possible worlds are ${definitions.keys.joinToString(",")}")
        return@runBlocking
    }
    val context = Context(Tracers.WHITTED.create, Renderers.SEQUENTIAL.create, Resolution.RESOLUTION_1080)
    Render.render(args[0], outputPngFileName(args[0]), context)
}

