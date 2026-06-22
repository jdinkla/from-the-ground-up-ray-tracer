package net.dinkla.raytracer.mappings

import net.dinkla.raytracer.math.Point3D

/**
 * Maps a hit point on an axis-aligned rectangle onto an image. The rectangle spans
 * `[-[uExtent], +[uExtent]]` along its [uAxis] and `[-[vExtent], +[vExtent]]` along its [vAxis];
 * those coordinates of the local hit point are normalised to [0,1] and then to (row, column).
 *
 * By default the rectangle lies in the xz-plane (the book's orientation for a ground rectangle):
 * x selects the column and z selects the row. Mirrors Suffern's `RectangularMap` (Ray Tracing from
 * the Ground Up, ch. 29).
 */
class RectangularMap(
    private val uAxis: Axis = Axis.X,
    private val vAxis: Axis = Axis.Z,
    private val uExtent: Double = 1.0,
    private val vExtent: Double = 1.0,
) : Mapping {
    /** Which coordinate of the local hit point a rectangle axis reads. */
    enum class Axis {
        X,
        Y,
        Z,
    }

    private fun Point3D.component(axis: Axis): Double =
        when (axis) {
            Axis.X -> x
            Axis.Y -> y
            Axis.Z -> z
        }

    override fun getTexelCoordinates(
        localHitPoint: Point3D,
        hres: Int,
        vres: Int,
    ): Texel {
        // Normalise the two in-plane coordinates from [-extent, +extent] to [0,1].
        val u = (localHitPoint.component(uAxis) + uExtent) / (2.0 * uExtent)
        val v = (localHitPoint.component(vAxis) + vExtent) / (2.0 * vExtent)

        val column = ((hres - 1) * u).toInt()
        val row = ((vres - 1) * (1.0 - v)).toInt()
        return Texel(row, column)
    }
}
