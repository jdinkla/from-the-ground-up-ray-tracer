package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Ray
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

    // The tmin-reporting trace variant is intentionally not overridden: MultipleObjects exposes no
    // nearest-hit distance, so it inherits Tracer's default (delegate to the two-argument trace).
    // It previously threw UnsupportedOperationException, a fat-interface stub no caller exercised
    // (TASK-63).
}
