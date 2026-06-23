package net.dinkla.raytracer.world

import net.dinkla.raytracer.films.Film
import kotlin.time.Duration

/**
 * Structured performance data for a single render. The render time is a *metric* — a typed value the
 * caller can display, aggregate or assert on — not a log line formatted at the measurement site. Each
 * caller (the CLI, the Swing UI, a test) decides how to present it.
 *
 * @property duration monotonic elapsed time of the render itself (excludes world build and file save).
 * @property counts snapshot of the per-event tallies
 *   [Counter][net.dinkla.raytracer.utilities.Counter] accumulated up to the end of the render
 *   (acceleration-structure build and hit-test events).
 */
data class RenderStats(
    val duration: Duration,
    val counts: Map<String, Int> = emptyMap(),
)

/**
 * The outcome of rendering a [WorldDefinition]: the rendered [film], the [world] that produced it (its
 * lights, objects and camera remain inspectable for the caller), and the [stats] for that render.
 */
data class RenderResult(
    val film: Film,
    val world: World,
    val stats: RenderStats,
)
