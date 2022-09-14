package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.interfaces.Counter
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.WrappedDouble
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.world.IWorld

class Whitted(var world: IWorld) : Tracer {

    override fun trace(ray: Ray): Color {
        Counter.count("Whitted.trace1")
        return trace(ray, 0)
    }

    override fun trace(ray: Ray, depth: Int): Color {
        Counter.count("Whitted.trace2")
        return trace(ray, WrappedDouble.createMax(), depth)
    }

    override fun trace(ray: Ray, tmin: WrappedDouble, depth: Int): Color {
        Counter.count("Whitted.trace3")
//        var color = build.backgroundColor
        val color: Color
        if (world.shouldStopRecursion(depth)) {
            color = Color.BLACK
        } else {
            val sr = Shade()
            val hit = world.hit(ray, sr)
            if (hit) {
                sr.depth = depth
                sr.ray = ray
                tmin.value = sr.t
                if (null == sr.material) {
                    Logger.error("Material is NULL for ray $ray and sr $sr")
                    color = Color.RED
                } else {
                    color = sr.material?.shade(world, sr) ?: world.backgroundColor
                }
            } else {
                // No hit -> Background
                tmin.value = MathUtils.K_HUGEVALUE
                color = world.backgroundColor
            }
        }
        /*
        double ff =  Math.sqrt(tmin.getValue() * f);
        color =  color.plus(fc.minus(ff));
        */
        return color
//        return (if (color == null) build.backgroundColor else color)
    }
}
