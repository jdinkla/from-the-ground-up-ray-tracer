package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.worlds.World

class RayCast(world: World) : Tracer(world) {

    init {
        throw RuntimeException("DO NOT USE")
    }

    override fun trace(ray: Ray, depth: Int): Color {
        val sr = Shade()
        if (world.hit(ray, sr)) {
            sr.ray = ray
            return sr.material.shade(world, sr)
        } else {
            return world.backgroundColor
        }
    }

}
