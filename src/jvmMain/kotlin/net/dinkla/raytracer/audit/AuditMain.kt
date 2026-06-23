package net.dinkla.raytracer.audit

import net.dinkla.raytracer.cameras.IColorCorrector
import net.dinkla.raytracer.examples.worlds
import net.dinkla.raytracer.films.ColorGridFilm
import net.dinkla.raytracer.renderer.ISingleRayRenderer
import net.dinkla.raytracer.renderer.SequentialRenderer
import net.dinkla.raytracer.tracers.AreaLighting
import net.dinkla.raytracer.tracers.Whitted
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.world.Context
import net.dinkla.raytracer.world.World
import java.io.File
import java.io.OutputStream
import java.io.PrintStream

/**
 * Entry point for the `audit` Gradle task. Wires the real scene list and renderer into [SceneAuditor],
 * then prints the report to stdout and writes it to `build/reports/scene-audit.md`. This is glue: the
 * tested logic lives in the rest of the package; this file is excluded from coverage like the other
 * entry points.
 */

/** Small, fast frame — enough to tell a lit image from a black one without paying full-render cost. */
private val AUDIT_RESOLUTION = Resolution(height = 90)

/** A scene rendering ≥ 99.9% near-black is almost certainly broken, not merely dark. */
private const val SUSPECT_THRESHOLD = 0.999

fun main() {
    val realOut = System.out
    val catalog = ClassCatalog.scan()
    val definitions = worlds().sortedBy { it.id }
    realOut.println("Auditing ${definitions.size} scenes (health render at ${resolutionLabel()})…")

    val auditor =
        SceneAuditor(
            buildWorld = { definition ->
                realOut.println("  ${definition.id}")
                definition.world()
            },
            inspect = SceneInspector::inspect,
            renderStatus = ::renderStatus,
        )
    val model = suppressingStdout { auditor.audit(definitions, catalog, SUSPECT_THRESHOLD) }

    val markdown = model.toMarkdown(resolutionLabel())
    realOut.println()
    realOut.println(markdown)

    val output = File("build/reports/scene-audit.md")
    output.parentFile?.mkdirs()
    output.writeText(markdown)
    realOut.println("Wrote ${output.path}")
}

private fun resolutionLabel(): String = "${AUDIT_RESOLUTION.width}x${AUDIT_RESOLUTION.height}"

/**
 * Health-renders [world] at low resolution into a readable [ColorGridFilm] and reports its near-black
 * fraction. Stereo scenes use a different (double-width, composited) pipeline and are skipped here.
 */
private fun renderStatus(world: World): RenderStatus {
    if (world.stereoCamera != null) return RenderStatus.Skipped("stereo camera")
    world.viewPlane.numSamples = 1
    val tracerCreator =
        when (chooseTracer(world)) {
            AuditTracer.AREA -> { w: net.dinkla.raytracer.world.IWorld -> AreaLighting(w) }
            AuditTracer.WHITTED -> { w: net.dinkla.raytracer.world.IWorld -> Whitted(w) }
        }
    val rendererCreator = { single: ISingleRayRenderer, corrector: IColorCorrector ->
        SequentialRenderer(single, corrector)
    }
    val context = Context(tracerCreator, rendererCreator, AUDIT_RESOLUTION)
    context.adapt(world)
    world.initialize()
    val film = ColorGridFilm(AUDIT_RESOLUTION)
    requireNotNull(world.renderer) { "renderer not wired by Context.adapt" }.render(film)
    return RenderStatus.Rendered(BlackImageDetector.nearBlackFraction(film))
}

/** Silences the per-pixel/per-render [net.dinkla.raytracer.utilities.Logger] chatter (it prints to
 *  stdout) so it does not bury the report. Progress still goes to the captured real stdout. */
private fun <T> suppressingStdout(block: () -> T): T {
    val original = System.out
    val sink = PrintStream(object : OutputStream() { override fun write(b: Int) = Unit })
    System.setOut(sink)
    return try {
        block()
    } finally {
        System.setOut(original)
    }
}
