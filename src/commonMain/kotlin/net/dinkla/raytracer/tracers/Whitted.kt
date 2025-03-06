package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.WrappedDouble
import net.dinkla.raytracer.utilities.Counter
import net.dinkla.raytracer.utilities.Logger
import net.dinkla.raytracer.world.IWorld

class Whitted(
    var world: IWorld,
) : Tracer {
    override fun trace(
        ray: Ray,
        depth: Int,
    ): Color {
        Counter.count("Whitted.trace2")
        return trace(ray, WrappedDouble.createMax(), depth)
    }

    override fun trace(
        ray: Ray,
        tmin: WrappedDouble,
        depth: Int,
    ): Color {
        Counter.count("Whitted.trace3")
        val color: Color
        if (world.shouldStopRecursion(depth)) {
            return Color.BLACK
        }
        val sr = Shade()
        val hit = world.hit(ray, sr)
        if (hit) {
            sr.depth = depth
            sr.ray = ray
            tmin.value = sr.t
            if (null == sr.material) {
                Logger.error("Material is NULL for ray $ray and sr $sr")
                return Color.RED
            }
            color = sr.material?.shade(world, sr) ?: world.backgroundColor
            return color
        }
        tmin.value = MathUtils.K_HUGE_VALUE
        color = world.backgroundColor
        return color
    }
}
