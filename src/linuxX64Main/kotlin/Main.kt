
import kotlinx.coroutines.runBlocking
import net.dinkla.raytracer.renderer.Renderers
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.world.Context
import net.dinkla.raytracer.world.Render

@OptIn(ExperimentalStdlibApi::class)
fun main() = runBlocking {
    Logger.info("From-the-ground-up-raytracer on linux (isExperimentalMM: ${isExperimentalMM()}")
    val args = listOf("World10.kt", "out-linux.png")
    if (args.size != 2) {
        Logger.error("CommandLineUI expects input filename and output filename as arguments")
        return@runBlocking
    }
    val context = Context(Tracers.WHITTED.create, Renderers.SEQUENTIAL.create, Resolution.RESOLUTION_720)
    Render.render(args[0], args[1], context)
}