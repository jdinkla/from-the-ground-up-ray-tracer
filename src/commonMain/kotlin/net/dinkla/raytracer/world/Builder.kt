package net.dinkla.raytracer.world

import net.dinkla.raytracer.world.dsl.WorldScope

object Builder {
    fun build(build: WorldScope.() -> Unit): World {
        val scope = WorldScope()
        scope.build()
        return scope.world
    }
}

