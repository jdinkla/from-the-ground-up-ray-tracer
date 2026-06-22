package net.dinkla.raytracer.mappings

import net.dinkla.raytracer.math.MathUtils.INV_PI
import net.dinkla.raytracer.math.Point3D
import kotlin.math.acos
import kotlin.math.sqrt

/**
 * Maps a direction (a point on the unit sphere) onto a light-probe / angular-map image used as a
 * spherical environment map. In [MapType.LIGHT_PROBE] (regular) mode the full sphere of directions
 * folds onto a disk; in [MapType.PANORAMIC] mode it covers the whole image. The hit point is treated
 * as a unit direction `(x, y, z)`.
 *
 * Mirrors Suffern's `LightProbe::get_texel_coordinates` (Ray Tracing from the Ground Up, ch. 29).
 */
class LightProbe(
    private val mapType: MapType = MapType.LIGHT_PROBE,
) : Mapping {
    /** Whether the probe covers a disk (regular light probe) or the full image (panoramic). */
    enum class MapType {
        LIGHT_PROBE,
        PANORAMIC,
    }

    override fun getTexelCoordinates(
        localHitPoint: Point3D,
        hres: Int,
        vres: Int,
    ): Texel {
        val x = localHitPoint.x
        val y = localHitPoint.y
        val z = localHitPoint.z

        // beta is the angle from the +z axis (light probe) or -z axis (panoramic).
        val beta =
            when (mapType) {
                MapType.LIGHT_PROBE -> acos(z)
                MapType.PANORAMIC -> acos(-z)
            }
        val d = sqrt(x * x + y * y)
        val r = if (d > 0.0) (INV_PI * beta) / d else 0.0

        val u = (1.0 + r * x) * HALF
        val v = (1.0 + r * y) * HALF

        val column = ((hres - 1) * u).toInt()
        val row = ((vres - 1) * (1.0 - v)).toInt()
        return Texel(row, column)
    }

    companion object {
        private const val HALF = 0.5
    }
}
