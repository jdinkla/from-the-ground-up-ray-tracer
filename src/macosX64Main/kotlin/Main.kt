
import kotlinx.coroutines.runBlocking
import net.dinkla.raytracer.examples.definitions
import net.dinkla.raytracer.gui.outputPngFileName
import net.dinkla.raytracer.renderer.Renderer
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.utilities.CommandLine
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.utilities.Resolution.Companion.resolutions
import net.dinkla.raytracer.world.Context
import net.dinkla.raytracer.world.Render

class CommandLineMacOs(
    worlds: Collection<String>,
    tracers: Collection<Tracers>,
    renderers: Collection<Renderer>,
    resolutions: Collection<Resolution.Predefined>
) : CommandLine(worlds, tracers, renderers, resolutions) {
    override fun render(context: Context) {
        runBlocking {
            Render.render(world, outputPngFileName(world), context)
        }
    }
}

@OptIn(ExperimentalStdlibApi::class)
fun main(args: Array<String>) = runBlocking {
    Logger.info("From-the-ground-up-raytracer on Mac OS X (isExperimentalMM: ${isExperimentalMM()}")
    val renderers = Renderer.values().toList()
    val tracers = Tracers.values().toList()
    CommandLineMacOs(definitions.keys, tracers, renderers, resolutions).main(args)
}