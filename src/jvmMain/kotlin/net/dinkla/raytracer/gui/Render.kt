package net.dinkla.raytracer.gui

import net.dinkla.raytracer.films.Film
import net.dinkla.raytracer.gui.awt.AwtFilm
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object Render {

    fun render(worldDefinition: WorldDefinition): Pair<Film, World> {
        val world = worldDefinition.world()
        world.initialize()
        val film = AwtFilm(world.viewPlane.resolution)
        world.renderer?.render(film)
        return Pair(film, world)
    }

}