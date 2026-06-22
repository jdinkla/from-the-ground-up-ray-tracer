package net.dinkla.raytracer.objects

import net.dinkla.raytracer.math.MathUtils
import kotlin.math.acos
import kotlin.math.atan2

/**
 * Shared angular helpers for the "part" primitives ([PartSphere], [PartCylinder], [PartTorus]).
 *
 * A part object restricts a full primitive to an angular wedge. After a candidate intersection
 * is found, its hit point is converted to spherical-style angles and tested against the configured
 * limits; hits outside the wedge are rejected so the remaining surface is "cut away".
 *
 * Conventions (matching Suffern, *Ray Tracing from the Ground Up*, ch. 19):
 *  - **phi** is the azimuth around the y-axis, `atan2(x, z)` wrapped to `[0, 2π)`.
 *  - **theta** is the polar angle measured from the +y axis, `acos(y / r)` in `[0, π]`.
 */
internal object PartAngles {
    const val TWO_PI: Double = 2.0 * MathUtils.PI

    /** Azimuth of `(x, z)` around the y-axis, wrapped to `[0, 2π)`. */
    fun phi(
        x: Double,
        z: Double,
    ): Double {
        val raw = atan2(x, z)
        return if (raw < 0.0) raw + TWO_PI else raw
    }

    /** Polar angle from the +y axis for a point at height [y] on a sphere of the given [radius]. */
    fun theta(
        y: Double,
        radius: Double,
    ): Double = acos(MathUtils.clamp(y / radius, -1.0, 1.0))

    /** True when [phi] lies within the inclusive azimuth wedge `[phiMin, phiMax]`. */
    fun inPhiRange(
        phi: Double,
        phiMin: Double,
        phiMax: Double,
    ): Boolean = phi in phiMin..phiMax

    /** True when [theta] lies within the inclusive polar band `[thetaMin, thetaMax]`. */
    fun inThetaRange(
        theta: Double,
        thetaMin: Double,
        thetaMax: Double,
    ): Boolean = theta in thetaMin..thetaMax
}
