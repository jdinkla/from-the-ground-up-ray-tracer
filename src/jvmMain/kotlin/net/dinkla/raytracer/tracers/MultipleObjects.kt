package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.WrappedDouble
import net.dinkla.raytracer.world.World

class MultipleObjects(var world: World) : Tracer {

    override fun trace(ray: Ray): Color {
        val sr = Shade()
        return if (world.hit(ray, sr)) {
            sr.ray = ray
            sr.material?.shade(world, sr) ?: world.backgroundColor
        } else {
            world.backgroundColor
        }
    }

    override fun trace(ray: Ray, depth: Int): Color {
        throw RuntimeException("MultipleObjects.trace")
    }

    override fun trace(ray: Ray, tmin: WrappedDouble, depth: Int): Color {
        throw RuntimeException("MultipleObjects.trace")
    }

}