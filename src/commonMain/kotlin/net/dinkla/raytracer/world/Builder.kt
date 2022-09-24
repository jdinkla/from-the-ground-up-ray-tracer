package net.dinkla.raytracer.world

import net.dinkla.raytracer.world.dsl.WorldScope

object Builder {
    @Deprecated("remove")
    fun build(id: String, build: WorldScope.() -> Unit): World  = build(build)

    fun build(build: WorldScope.() -> Unit): World {
        val scope = WorldScope()
        scope.build()
        return scope.world
    }

}

