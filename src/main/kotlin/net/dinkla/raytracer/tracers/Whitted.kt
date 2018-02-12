package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.WrappedFloat
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.worlds.World
import org.apache.log4j.Logger

class Whitted(world: World) : Tracer(world) {

    override fun trace(ray: Ray): Color {
        Counter.count("Whitted.trace1")
        return trace(ray, 0)
    }

    override fun trace(ray: Ray, depth: Int): Color {
        Counter.count("Whitted.trace2")
        return trace(ray, WrappedFloat.createMax(), depth)
    }

    override fun trace(ray: Ray, tmin: WrappedFloat, depth: Int): Color {
        //LOGGER.debug("trace " + ray + " at depth " + depth);
        Counter.count("Whitted.trace3")
//        var color = world.backgroundColor
        var color: Color
        if (depth > world.viewPlane.maxDepth) {
            color = Color.BLACK
        } else {
            val sr = Shade()
            val hit = world.hit(ray, sr)
            if (hit) {
                sr.depth = depth
                sr.ray = ray
                tmin.value = sr.t
                if (null == sr.material) {
                    LOGGER.error("Material is NULL for ray $ray and sr $sr")
                    color = Color.errorColor
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
//        return (if (color == null) world.backgroundColor else color)
    }

    companion object {

        internal val LOGGER = Logger.getLogger(Whitted::class.java)
    }

}
