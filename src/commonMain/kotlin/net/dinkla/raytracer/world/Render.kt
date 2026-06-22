package net.dinkla.raytracer.world

import net.dinkla.raytracer.examples.worldMap
import net.dinkla.raytracer.films.Film
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.renderer.IRenderer
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Timer

object Render {
    suspend fun render(
        fileNameIn: String,
        fileNameOut: String,
        context: Context,
    ) {
        Logger.info("Rendering $fileNameIn to $fileNameOut")
        val worldDefinition = requireWorldDef(fileNameIn)
        val (film, _) = render(worldDefinition, context)
        film.save(fileNameOut)
    }

    fun render(
        worldDefinition: WorldDefinition,
        context: Context,
    ): Pair<Film, World> {
        val world = worldDefinition.world()
        context.adapt(world)
        world.initialize()
        val film = Film(context.resolution)
        val renderer =
            requireNotNull(world.renderer) { "World.renderer not set; context.adapt(world) must run first" }
        render(film, renderer)
        return Pair(film, world)
    }

    fun render(
        film: IFilm,
        renderer: IRenderer,
    ) {
        val timer = Timer()
        timer.start()
        renderer.render(film)
        timer.stop()
        Logger.info("rendering took " + timer.duration + " ms")

        Counter.stats(30)

        Logger.info("took " + timer.duration + " [ms]")
        Counter.reset()
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
