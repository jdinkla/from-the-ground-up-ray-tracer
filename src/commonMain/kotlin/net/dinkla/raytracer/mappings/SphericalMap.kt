package net.dinkla.raytracer.mappings

import net.dinkla.raytracer.math.MathUtils.PI
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import kotlin.math.acos
import kotlin.math.atan2

/**
 * Maps a hit point on a sphere centred at the origin onto a rectangular image using
 * latitude/longitude (equirectangular) coordinates. A point's azimuth around the y-axis selects the
 * column and its polar angle from the +y axis selects the row. The hit point is normalised first, so
 * the sphere may have any radius (the radius cancels); for a unit sphere this is a no-op.
 *
 * Mirrors Suffern's `SphericalMap::get_texel_coordinates` (Ray Tracing from the Ground Up, ch. 29).
 */
class SphericalMap : Mapping {
    override fun getTexelCoordinates(
        localHitPoint: Point3D,
        hres: Int,
        vres: Int,
    ): Texel {
        // Direction from the sphere centre (origin) to the hit point, on the unit sphere.
        val d = Vector3D(localHitPoint).normalize()

        // Spherical coordinates of the direction.
        val theta = acos(d.y)
        val phi = atan2(d.x, d.z).let { if (it < 0.0) it + TWO_PI else it }

        // Texture coordinates in [0,1].
        val u = phi * INV_TWO_PI
        val v = 1.0 - theta * INV_PI

        // Image (row, column), with row measured from the top.
        val column = ((hres - 1) * u).toInt()
        val row = ((vres - 1) * (1.0 - v)).toInt()
        return Texel(row, column)
    }

    companion object {
        private const val TWO_PI = 2.0 * PI
        private const val INV_TWO_PI = 1.0 / TWO_PI
        private const val INV_PI = 1.0 / PI
    }
}
