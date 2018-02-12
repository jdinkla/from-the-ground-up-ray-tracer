package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.WrappedFloat
import net.dinkla.raytracer.worlds.World

class MultipleObjects(world: World) : Tracer(world) {

    override fun trace(ray: Ray): Color {
        assert(null != ray)
        assert(null != ray.origin)
        assert(null != ray.direction)
        val sr = Shade()
        if (world.hit(ray, sr)) {
            sr.ray = ray
            return sr.material?.shade(world, sr) ?: world.backgroundColor
        } else {
            return world.backgroundColor
        }
    }

    override fun trace(ray: Ray, depth: Int): Color {
        throw RuntimeException("MultipleObjects.trace")
    }

    override fun trace(ray: Ray, tmin: WrappedFloat, depth: Int): Color {
        throw RuntimeException("MultipleObjects.trace")
    }

}