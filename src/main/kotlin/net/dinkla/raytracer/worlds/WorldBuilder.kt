package net.dinkla.raytracer.worlds

import java.io.File

class WorldBuilder(val world: World) {

    fun build(file: File) {

    }

    companion object {
        fun create(file: File) : World = World()

        fun world(id: String, build: WorldScope.() -> Unit): World {
            val scope = WorldScope(id)
            scope.build()
            return scope.world
        }
    }
}

