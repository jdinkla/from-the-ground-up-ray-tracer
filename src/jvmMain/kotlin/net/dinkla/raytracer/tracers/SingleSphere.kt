package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.WrappedDouble
import net.dinkla.raytracer.world.IWorld

class SingleSphere(var world: IWorld) : Tracer {

    override fun trace(ray: Ray): Color = if (world.hit(ray, Shade())) {
        Color.RED
    } else {
        world.backgroundColor
    }

    override fun trace(ray: Ray, depth: Int): Color = world.backgroundColor

    override fun trace(ray: Ray, tmin: WrappedDouble, depth: Int): Color = world.backgroundColor

}
