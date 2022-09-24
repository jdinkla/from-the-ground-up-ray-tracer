package net.dinkla.raytracer.world

import net.dinkla.raytracer.examples.worldDef
import net.dinkla.raytracer.films.Film
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.renderer.IRenderer
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Timer

object Render {
    suspend fun render(fileNameIn: String, fileNameOut: String, context: Context) {
        Logger.info("Rendering $fileNameIn to $fileNameOut")
        val worldDefinition = worldDef(fileNameIn)
        if (null == worldDefinition) {
            Logger.warn("WorldDef $fileNameIn is not known")
        } else {
            val (film, _) = render(worldDefinition, context)
            film.save(fileNameOut)
        }
    }

    fun render(worldDefinition: WorldDefinition, context: Context): Pair<Film, World> {
        val world = worldDefinition.world()
        context.adapt(world)
        world.initialize()
        val film = Film(context.resolution)
        render(film, world.renderer!!)
        return Pair(film, world)
    }

    fun render(film: IFilm, renderer: IRenderer) {
        val timer = Timer()
        timer.start()
        renderer.render(film)
        timer.stop()
        Logger.info("rendering took " + timer.duration + " ms")

        Counter.stats(30)

        Logger.info("took " + timer.duration + " [ms]")
        Counter.reset()
    }
}