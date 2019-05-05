package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.WrappedDouble
import net.dinkla.raytracer.world.World
import org.slf4j.LoggerFactory

interface Tracer {

    abstract fun trace(ray: Ray, depth: Int): Color

    open fun trace(ray: Ray, tmin: WrappedDouble, depth: Int): Color = trace(ray, depth)

    open fun trace(ray: Ray): Color = trace(ray, 0)

}
