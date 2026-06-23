package net.dinkla.raytracer
import kotlinx.coroutines.runBlocking
import net.dinkla.raytracer.examples.worldMap
import net.dinkla.raytracer.renderer.Renderer
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.ui.CommandLine
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.utilities.Resolution.Companion.resolutions
import net.dinkla.raytracer.utilities.outputPngFileName
import net.dinkla.raytracer.utilities.printStats
import net.dinkla.raytracer.world.Context
import net.dinkla.raytracer.world.Render
import net.dinkla.raytracer.world.scripting.SceneResolver
import java.io.File

private const val COUNTER_COLUMN_WIDTH = 30

class Main(
    worlds: Collection<String>,
    tracers: Collection<Tracers>,
    renderers: Collection<Renderer>,
    resolutions: Collection<Resolution.Predefined>,
) : CommandLine(worlds, tracers, renderers, resolutions) {
    override fun render(context: Context) {
        runBlocking {
            // `world` may be a built-in scene id ("YellowAndRedSphere.kt") or the path of an external
            // *.scene.kts file ("scenes/Sample.scene.kts"); base the output name on the bare file
            // name so a path does not leak directory separators into the PNG path.
            val outputBase = File(world).name
            val stats = Render.render(world, "../${outputPngFileName(outputBase)}", context) { id ->
                SceneResolver.resolveWorld(id)
            }
            // Measurement is decoupled from presentation: Render returns the metric, the CLI logs it.
            Logger.info("rendering took ${stats.duration.inWholeMilliseconds} ms")
            printStats(stats.counts, COUNTER_COLUMN_WIDTH)
        }
    }
}

fun main(args: Array<String>) {
    Logger.info("From-the-ground-up-raytracer on JVM")
    Main(worldMap.keys, Tracers.entries, Renderer.entries, resolutions).main(args)
}
