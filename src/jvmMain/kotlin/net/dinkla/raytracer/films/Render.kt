package net.dinkla.raytracer.films

import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object Render {

    fun render(worldDefinition: WorldDefinition): Pair<Film, World> {
        val world = worldDefinition.world()
        world.initialize()
        val film = Film(world.viewPlane.resolution)
        world.renderer?.render(film)
        return Pair(film, world)
    }

}