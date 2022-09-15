package net.dinkla.raytracer.world

import net.dinkla.raytracer.world.dsl.WorldScope

object Builder {
    fun build(id: String, build: WorldScope.() -> Unit): World {
        val scope = WorldScope(id)
        scope.build()
        return scope.world
    }
}

