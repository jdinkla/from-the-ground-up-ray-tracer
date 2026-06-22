package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import kotlin.math.abs
import kotlin.math.floor

/**
 * A 2D checkerboard painted on the xz-plane: the local hit point's x and z coordinates are divided
 * into square cells of edge [size], and adjacent cells alternate between [color1] and [color2]. A
 * configurable grout line of half-width [lineWidth] and colour [lineColor] is drawn along the cell
 * boundaries, so the texture also reads as a tiled floor with mortar lines.
 *
 * Unlike [Checker3D] (a solid 3D checker keyed on all three coordinates), this is a surface checker:
 * it only uses x and z, so it tiles a horizontal plane uniformly regardless of height. The hit point
 * is read in local coordinates; as documented for TASK-18.1, objects are assumed origin-centred and
 * axis-aligned (Instance-local coordinates are not yet threaded).
 *
 * Mirrors Suffern's `PlaneChecker` (Ray Tracing from the Ground Up, ch. 29).
 */
data class PlaneChecker(
    val size: Double = 1.0,
    val lineWidth: Double = 0.0,
    val color1: Color = Color.WHITE,
    val color2: Color = Color.BLACK,
    val lineColor: Color = Color.BLACK,
) : Texture {
    override fun getColor(sr: IShade): Color = colorAt(sr.localHitPoint.x, sr.localHitPoint.z)

    /**
     * The checker colour for a point at plane coordinates ([x], [z]). Pure and side-effect free so the
     * cell-selection and grout-line logic can be unit-tested directly without building a hit record.
     */
    fun colorAt(
        x: Double,
        z: Double,
    ): Color {
        val ix = floor(x / size).toInt()
        val iz = floor(z / size).toInt()

        if (lineWidth > 0.0 && (onLine(x) || onLine(z))) {
            return lineColor
        }

        val isEven = (ix + iz) and 1 == 0
        return if (isEven) color1 else color2
    }

    /** True when [coordinate] lies within [lineWidth] of a cell boundary (a grout line). */
    private fun onLine(coordinate: Double): Boolean {
        val cell = coordinate / size
        val fractional = cell - floor(cell)
        val distanceToBoundary = minOf(fractional, 1.0 - fractional) * size
        return abs(distanceToBoundary) < lineWidth
    }
}
