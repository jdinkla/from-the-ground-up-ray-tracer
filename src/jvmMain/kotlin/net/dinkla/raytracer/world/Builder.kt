package net.dinkla.raytracer.world

import net.dinkla.raytracer.utilities.AppProperties
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.world.dsl.WorldScope

object Builder {

    private val width = AppProperties.getAsInteger("render.resolution.width")
    private val height = AppProperties.getAsInteger("render.resolution.height")

    var resolution: Resolution = Resolution(width, height)

    fun build(id: String, resolution: Resolution, build: WorldScope.() -> Unit): World {
        val scope = WorldScope(id, resolution)
        scope.build()
        return scope.world
    }

    fun build(id: String, build: WorldScope.() -> Unit): World = build(id, resolution, build)

}

