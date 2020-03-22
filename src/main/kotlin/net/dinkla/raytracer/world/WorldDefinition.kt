package net.dinkla.raytracer.world

interface WorldDefinition {
    fun world(): World

    fun init(): World {
        val world = world()
        world.initialize()
        return world
    }

}