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
import net.dinkla.raytracer.world.Context
import net.dinkla.raytracer.world.Render

class Main(
    worlds: Collection<String>,
    tracers: Collection<Tracers>,
    renderers: Collection<Renderer>,
    resolutions: Collection<Resolution.Predefined>
) : CommandLine(worlds, tracers, renderers, resolutions) {
    override fun render(context: Context) {
        runBlocking {
            Render.render(world, "../${outputPngFileName(world)}", context)
        }
    }
}

fun main(args: Array<String>) {
    Logger.info("From-the-ground-up-raytracer on JVM")
    Main(worldMap.keys, Tracers.entries, Renderer.entries, resolutions).main(args)
}
