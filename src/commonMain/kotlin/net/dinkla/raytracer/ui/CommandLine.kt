package net.dinkla.raytracer.ui

import com.github.ajalt.clikt.core.CliktCommand
import com.github.ajalt.clikt.parameters.options.default
import com.github.ajalt.clikt.parameters.options.option
import com.github.ajalt.clikt.parameters.options.validate
import com.github.ajalt.clikt.parameters.types.choice
import net.dinkla.raytracer.renderer.Renderer
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.world.Context
import java.io.File

@Suppress("SpreadOperator")
abstract class CommandLine(
    private val worlds: Collection<String>,
    val tracers: Collection<Tracers>,
    private val renderers: Collection<Renderer>,
    private val resolutions: Collection<Resolution.Predefined>,
) : CliktCommand() {
    // `--world` accepts either a built-in scene id (from the classgraph worldMap) or the path of an
    // existing external `*.scene.kts` file (TASK-17). It is no longer a strict `.choice()` over the
    // built-in ids — that would reject file paths — but an unknown value that is neither a known id
    // nor an existing file still fails fast with the same actionable message as before (TASK-15).
    val world by
        option(help = "world definition id or path to an external *.scene.kts file (default: YellowAndRedSphere.kt)")
            .default("YellowAndRedSphere.kt")
            .validate { value ->
                require(isAcceptableWorldArg(value, worlds)) {
                    "Unknown world '$value'. Pass a built-in scene id or the path of an existing " +
                        "*.scene.kts file. Built-in scene ids: ${worlds.sorted().joinToString(", ")}"
                }
            }
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

    private fun determineResolution(): Resolution = Resolution.fromId(resolution)
}

/**
 * Validates a `--world` value: it is acceptable if it is a known built-in scene [id][worldIds] or the
 * path of an existing file (an external `*.scene.kts` scene, TASK-17). The file-existence check is
 * injected via [fileExists] so this pure decision can be unit-tested without touching the filesystem;
 * production uses the default that consults the real filesystem.
 */
internal fun isAcceptableWorldArg(
    value: String,
    worldIds: Collection<String>,
    fileExists: (String) -> Boolean = { File(it).isFile },
): Boolean = value in worldIds || fileExists(value)
