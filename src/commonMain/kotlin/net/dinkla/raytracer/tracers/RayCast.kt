package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.world.IWorld

class RayCast(
    var world: IWorld,
) : Tracer {
    init {
        throw UnsupportedOperationException("RayCast tracer is deprecated and must not be used")
    }

    override fun trace(
        ray: Ray,
        depth: Int,
    ): Color {
        val sr = Shade()
        return if (world.hit(ray, sr)) {
            sr.ray = ray
            sr.material?.shade(world, sr) ?: world.backgroundColor
        } else {
            world.backgroundColor
        }
    }
}
