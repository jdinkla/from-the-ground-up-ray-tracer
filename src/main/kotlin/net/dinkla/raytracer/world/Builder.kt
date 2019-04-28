package net.dinkla.raytracer.world

import java.io.File

class Builder(val world: World) {

    fun build(file: File) {

    }

    companion object {
        fun create(file: File) : World = World()

        fun build(id: String, build: WorldScope.() -> Unit): World {
            val scope = WorldScope(id)
            scope.build()
            return scope.world
        }
    }
}

