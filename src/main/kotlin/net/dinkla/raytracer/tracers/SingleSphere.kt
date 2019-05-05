package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.WrappedDouble
import net.dinkla.raytracer.world.World

class SingleSphere(world: World) : Tracer(world) {

    override fun trace(ray: Ray): Color {
        return if (world.hit(ray, Shade())) {
            Color.ERROR
        } else {
            world.backgroundColor
        }
    }

    override fun trace(ray: Ray, depth: Int): Color {
        return world.backgroundColor
    }

    override fun trace(ray: Ray, tmin: WrappedDouble, depth: Int): Color {
        return world.backgroundColor
    }

}
