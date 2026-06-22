package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.MathUtils.PI
import net.dinkla.raytracer.math.Vector3D
import kotlin.math.acos
import kotlin.math.atan2

/**
 * A 2D checkerboard painted on a sphere centred at the origin. The hit point is turned into a
 * direction on the unit sphere and then into latitude/longitude texture coordinates (the same
 * convention as [net.dinkla.raytracer.mappings.SphericalMap]); the surface is divided into
 * [numHorizontalCheckers] bands of longitude and [numVerticalCheckers] bands of latitude, and
 * adjacent cells alternate between [color1] and [color2]. A grout line of half-width [lineWidth]
 * (measured in checker cells) and colour [lineColor] is drawn along the band boundaries.
 *
 * The hit point is normalised, so the sphere may have any radius, but it must be centred at the
 * origin (Instance-local coordinates are not yet threaded — see TASK-18.1).
 *
 * Mirrors Suffern's `SphereChecker` (Ray Tracing from the Ground Up, ch. 29).
 */
data class SphereChecker(
    val numHorizontalCheckers: Int = 20,
    val numVerticalCheckers: Int = 10,
    val lineWidth: Double = 0.0,
    val color1: Color = Color.WHITE,
    val color2: Color = Color.BLACK,
    val lineColor: Color = Color.BLACK,
) : Texture {
    override fun getColor(sr: IShade): Color {
        val d = Vector3D(sr.localHitPoint).normalize()
        val theta = acos(d.y)
        val phi = atan2(d.x, d.z).let { if (it < 0.0) it + TWO_PI else it }

        // Texture coordinates in [0,1].
        val u = phi * INV_TWO_PI
        val v = 1.0 - theta * INV_PI
        return colorAt(u, v)
    }

    /**
     * The checker colour for texture coordinates ([u], [v]) in [0,1]. Pure and side-effect free so the
     * band selection and grout-line logic can be unit-tested directly without building a hit record.
     */
    fun colorAt(
        u: Double,
        v: Double,
    ): Color {
        val ix = (u * numHorizontalCheckers).toInt()
        val iz = (v * numVerticalCheckers).toInt()

        if (lineWidth > 0.0 && (onLine(u * numHorizontalCheckers) || onLine(v * numVerticalCheckers))) {
            return lineColor
        }

        val isEven = (ix + iz) and 1 == 0
        return if (isEven) color1 else color2
    }

    /** True when [cell] (a coordinate already scaled to cell units) lies within [lineWidth] of a cell boundary. */
    private fun onLine(cell: Double): Boolean {
        val fractional = cell - cell.toInt()
        return fractional < lineWidth || fractional > 1.0 - lineWidth
    }

    companion object {
        private const val TWO_PI = 2.0 * PI
        private const val INV_TWO_PI = 1.0 / TWO_PI
        private const val INV_PI = 1.0 / PI
    }
}
