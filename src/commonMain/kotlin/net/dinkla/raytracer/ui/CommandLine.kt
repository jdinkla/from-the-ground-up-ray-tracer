package net.dinkla.raytracer.ui

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import net.dinkla.raytracer.renderer.Renderer
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.world.Context

@Suppress("SpreadOperator")
abstract class CommandLine(
    worlds: Collection<String>,
    val tracers: Collection<Tracers>,
    private val renderers: Collection<Renderer>,
    private val resolutions: Collection<Resolution.Predefined>,
) : CliktCommand() {
    val world by option(help = "world definition id").choice(*worlds.toTypedArray()).default("World20.kt")
    val tracer by option(help = "tracer").choice(*tracers.map { it.name }.toTypedArray()).default("WHITTED")
    val renderer by option(help = "renderers").choice(*renderers.map { it.name }.toTypedArray()).default("SEQUENTIAL")
    val resolution by option(help = "resolution").choice(*resolutions.map { it.id }.toTypedArray()).default("1080p")

    override fun run() {
        val usedRenderer = determineRenderer()
        val usedTracer = determineTracer()
        val usedResolution = determineResolution()

        Logger.info("Using renderer $usedRenderer")
        Logger.info("Using tracer $usedTracer")
        Logger.info("Using resolution $usedResolution")

        val context = Context(usedTracer.create, usedRenderer.creator, usedResolution)
        render(context)
    }

    abstract fun render(context: Context)

    private fun determineRenderer(): Renderer = renderers.first { it.name == renderer }

    private fun determineTracer(): Tracers = tracers.first { it.name == tracer }

    private fun determineResolution(): Resolution = resolutions.first { it.id == resolution }.create()
}
