package net.dinkla.raytracer.world

import net.dinkla.raytracer.films.Film

object Render {

    fun render(worldDefinition: WorldDefinition, context: Context): Pair<Film, World> =
        render(worldDefinition, context, {}, {})

    fun <T> render(
        worldDefinition: WorldDefinition,
        context: Context,
        beforeRender: (Film) -> T,
        afterRender: (T) -> Unit
    ): Pair<Film, World> {
        val world = worldDefinition.world()
        context.adapt(world)
        world.initialize()
        val film = Film()
        film.resolution = world.viewPlane.resolution
        val intermediate = beforeRender(film)
        world.renderer?.render(film)
        afterRender(intermediate)
        return Pair(film, world)
    }

}