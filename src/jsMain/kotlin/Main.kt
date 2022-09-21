
import com.soywiz.korio.async.runBlockingNoSuspensions
import net.dinkla.raytracer.examples.definitions
import net.dinkla.raytracer.gui.outputPngFileName
import net.dinkla.raytracer.renderer.Renderer
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.utilities.CommandLine
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.world.Context
import net.dinkla.raytracer.world.Render
import node.process.process

class CommandLineJs(
    worlds: Collection<String>,
    tracers: Collection<Tracers>,
    renderers: Collection<Renderer>,
    resolutions: Collection<Resolution.Predefined>
) : CommandLine(worlds, tracers, renderers, resolutions) {
    override fun render(context: Context) {
        runBlockingNoSuspensions {
            Render.render(world, outputPngFileName(world), context)
        }
    }
}

fun main() {
    Logger.info("From-the-ground-up-raytracer on node/JavaScript")
    val args = process.argv.toList().drop(2)
    val renderers = Renderer.values().toList()
    val tracers = Tracers.values().toList()
    CommandLineJs(definitions.keys, tracers, renderers, Resolution.resolutions).main(args)
}
