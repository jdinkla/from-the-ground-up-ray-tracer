package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.world.World

class RayCast(var world: World) : Tracer {

    init {
        throw RuntimeException("DO NOT USE")
    }

    override fun trace(ray: Ray, depth: Int): Color {
        val sr = Shade()
        return if (world.hit(ray, sr)) {
            sr.ray = ray
            sr.material?.shade(world, sr) ?: world.backgroundColor
        } else {
            world.backgroundColor
        }
    }

}
