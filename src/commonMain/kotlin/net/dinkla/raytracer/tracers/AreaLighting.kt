package net.dinkla.raytracer.tracers

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.world.IWorld

class AreaLighting(
    var world: IWorld,
) : Tracer {
    override fun trace(
        ray: Ray,
        depth: Int,
    ): Color =
        if (world.shouldStopRecursion(depth)) {
            Color.BLACK
        } else {
            val sr = Shade()
            if (world.hit(ray, sr)) {
                sr.depth = depth
                sr.ray = ray
                if (null == sr.material) {
                    throw AssertionError()
                } else {
                    sr.material?.areaLightShade(world, sr) ?: world.backgroundColor
                }
            } else {
                world.backgroundColor
            }
        }
}
