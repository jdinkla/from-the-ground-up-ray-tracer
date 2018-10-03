package net.dinkla.raytracer.worlds

import java.io.File

class WorldBuilder(val world: World) {

    fun build(file: File) {

    }

    companion object {
        fun create(file: File) : World = World()
    }
}

