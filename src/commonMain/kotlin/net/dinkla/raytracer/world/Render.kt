package net.dinkla.raytracer.world

import net.dinkla.raytracer.examples.worldMap
import net.dinkla.raytracer.films.Film
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.renderer.CancellationToken
import net.dinkla.raytracer.renderer.IRenderer
import net.dinkla.raytracer.renderer.NoCancellation
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.Logger
import kotlin.time.Duration
import kotlin.time.measureTime
import kotlin.time.measureTimedValue

object Render {
    suspend fun render(
        fileNameIn: String,
        fileNameOut: String,
        context: Context,
        resolveWorld: (String) -> WorldDefinition = ::requireWorldDef,
    ): RenderStats {
        Logger.info("Rendering $fileNameIn to $fileNameOut")
        val worldDefinition = resolveWorld(fileNameIn)
        val result = render(worldDefinition, context)
        result.film.save(fileNameOut)
        return result.stats
    }

    fun render(
        worldDefinition: WorldDefinition,
        context: Context,
    ): RenderResult {
        val world = worldDefinition.world()
        context.adapt(world)
        world.initialize()
        if (world.stereoCamera != null) {
            // Stereo scenes render two eye views and composite them; the output dimensions differ
            // (double width for side-by-side). All other (non-stereo) scenes use the single-camera
            // path below, unchanged.
            val (film, duration) = measureTimedValue { StereoRender.render(world, context) }
            return RenderResult(film, world, finishStats(duration))
        }
        val film = Film(context.resolution)
        val renderer =
            requireNotNull(world.renderer) { "World.renderer not set; context.adapt(world) must run first" }
        return RenderResult(film, world, render(film, renderer))
    }

    /**
     * Renders [film] with [renderer] and returns the [RenderStats] for that render — a monotonic
     * elapsed [Duration][kotlin.time.Duration] plus a snapshot of the [Counter] tallies. Measurement
     * is decoupled from presentation: this returns the metric, it does not log it.
     */
    fun render(
        film: IFilm,
        renderer: IRenderer,
        cancellation: CancellationToken = NoCancellation,
    ): RenderStats {
        val duration = measureTime { renderer.render(film, cancellation) }
        return finishStats(duration)
    }

    /** Snapshots the accumulated [Counter] tallies into a [RenderStats] and resets the counter for the
     *  next render — preserving the long-standing per-render reset semantics. */
    private fun finishStats(duration: Duration): RenderStats {
        val stats = RenderStats(duration, Counter.snapshot())
        Counter.reset()
        return stats
    }

    private fun requireWorldDef(id: String): WorldDefinition = requireWorldDef(id, worldMap)
}

/**
 * Resolves a scene id against the [available] scene map, failing fast with a clear, actionable
 * message when the id is unknown instead of silently producing no output. Pure validation logic so it
 * can be unit-tested against a fabricated map without scanning the example scenes; [Render] supplies
 * the real `worldMap`.
 */
internal fun requireWorldDef(
    id: String,
    available: Map<String, WorldDefinition>,
): WorldDefinition =
    available[id]
        ?: throw IllegalArgumentException(
            "Unknown world '$id'. Run with --help to list available scenes " +
                "(scene ids are the scene object file names, e.g. 'YellowAndRedSphere.kt').",
        )
