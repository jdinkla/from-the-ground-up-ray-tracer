package net.dinkla.raytracer.world

interface WorldDefinition {
    val id: String
    fun world(): World
}
