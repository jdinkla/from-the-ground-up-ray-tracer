package net.dinkla.raytracer.world

import net.dinkla.raytracer.films.Film

object Render {

    fun render(worldDefinition: WorldDefinition): Pair<Film, World> {
        val world = worldDefinition.world()
        world.initialize()
        val film = Film()
        film.resolution = world.viewPlane.resolution
        world.renderer?.render(film)
        return Pair(film, world)
    }

}