package net.dinkla.raytracer.textures

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Point3D

/**
 * A colour-ramp texture: a scalar derived from the hit point is mapped onto a linear gradient between
 * [color1] (at scalar 0) and [color2] (at scalar 1). The scalar is the chosen [axis] of the local
 * hit point, multiplied by [frequency] and wrapped into `[0,1)`, so the gradient repeats as a
 * periodic banding along that axis.
 *
 * This is the value-driven (non-noise) form of the book's `Ramp` texture: the gradient is indexed by
 * a coordinate, not by a noise function (noise-modulated ramps are TASK-18.3). Keeping the scalar →
 * colour map a pure function makes the interpolation directly unit-testable.
 *
 * Mirrors Suffern's `Ramp` (Ray Tracing from the Ground Up, ch. 29), without the noise term.
 */
data class Ramp(
    val color1: Color = Color.BLACK,
    val color2: Color = Color.WHITE,
    val axis: Axis = Axis.Y,
    val frequency: Double = 1.0,
) : Texture {
    /** Which coordinate of the local hit point drives the ramp. */
    enum class Axis {
        X,
        Y,
        Z,
    }

    override fun getColor(sr: IShade): Color {
        val coordinate = sr.localHitPoint.component(axis) * frequency
        val scalar = coordinate - kotlin.math.floor(coordinate)
        return colorAt(scalar)
    }

    /**
     * The ramp colour for a normalised scalar in `[0,1]`: a linear interpolation from [color1] at 0 to
     * [color2] at 1. Pure and side-effect free so the interpolation can be unit-tested directly. The
     * scalar is clamped to `[0,1]` so out-of-range inputs map to the endpoints.
     */
    fun colorAt(scalar: Double): Color {
        val t = scalar.coerceIn(0.0, 1.0)
        return color1 * (1.0 - t) + color2 * t
    }

    private fun Point3D.component(axis: Axis): Double =
        when (axis) {
            Axis.X -> x
            Axis.Y -> y
            Axis.Z -> z
        }
}
