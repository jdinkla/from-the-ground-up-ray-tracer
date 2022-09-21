package net.dinkla.raytracer.utilities

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.types.choice
import net.dinkla.raytracer.renderer.Renderer
import net.dinkla.raytracer.renderer.createRenderer
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Context

abstract class CommandLine(
    val worlds: Collection<String>,
    val tracers: Collection<Tracers>,
    val renderers: Collection<Renderer>,
    val resolutions: Collection<Resolution.Predefined>
) : CliktCommand() {

    val world by option(help = "world definition id").default("World20.kt")
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

        val context = Context(usedTracer.create, createRenderer(usedRenderer), usedResolution)
        render(context)
    }

    abstract fun render(context: Context)

    private fun determineRenderer(): Renderer = renderers.first { it.name == renderer }
    private fun determineTracer(): Tracers = tracers.first { it.name == tracer }
    private fun determineResolution(): Resolution = resolutions.first { it.id == resolution }.create()
}
