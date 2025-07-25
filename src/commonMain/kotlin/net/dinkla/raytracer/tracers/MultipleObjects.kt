package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.WrappedDouble
import net.dinkla.raytracer.world.IWorld

class MultipleObjects(
    var world: IWorld,
) : Tracer {
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

    override fun trace(
        ray: Ray,
        tmin: WrappedDouble,
        depth: Int,
    ): Color = throw UnsupportedOperationException("MultipleObjects.trace")
}
