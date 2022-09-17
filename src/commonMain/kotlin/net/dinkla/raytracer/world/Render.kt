package net.dinkla.raytracer.world

import net.dinkla.raytracer.examples.worldDef
import net.dinkla.raytracer.films.Film
import net.dinkla.raytracer.films.IFilm
import net.dinkla.raytracer.objects.acceleration.kdtree.InnerNode
import net.dinkla.raytracer.renderer.IRenderer
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.utilities.Timer

object Render {
    fun render(worldDefinition: WorldDefinition, context: Context): Pair<Film, World> {
        val world = worldDefinition.world()
        context.adapt(world)
        world.initialize()
        val film = Film(world.viewPlane.resolution)
        world.renderer!!.render(film)
        return Pair(film, world)
    }

    suspend fun render(fileNameIn: String, fileNameOut: String, context: Context) {
        Logger.info("Rendering $fileNameIn to $fileNameOut")
        val worldDefinition = worldDef(fileNameIn)
        if (null == worldDefinition) {
            Logger.warn("WorldDef $fileNameIn is not known")
        } else {
            Logger.info("Using world ${worldDefinition.world().id}")
            val (film, _) = render(worldDefinition, context)
            film.save(fileNameOut)
        }
    }
    fun render(film: IFilm, renderer: IRenderer) {
        val timer = Timer()
        timer.start()
        renderer.render(film)
        timer.stop()
        Logger.info("rendering took " + timer.duration + " ms")

        // stats
        Counter.stats(30)

        Logger.info("Hits")
        InnerNode.hits.println()

        Logger.info("fails")
        InnerNode.fails.println()

        Logger.info("took " + timer.duration + " [ms]")
        Counter.reset()
    }
}