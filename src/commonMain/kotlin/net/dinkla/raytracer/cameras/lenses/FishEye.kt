package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.utilities.Resolution
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class FishEye(
    viewPlane: ViewPlane,
    eye: Point3D,
    uvw: Basis,
) : AbstractLens(viewPlane, eye, uvw) {
    private val maxPsi: Double = 1.0

    inner class RayDirection(
        val direction: Vector3D,
        val rSquared: Double = 0.0,
    )

    override fun getRaySampled(
        r: Int,
        c: Int,
        sp: Point2D,
    ): Ray? {
        var ray: Ray? = null
        val x = viewPlane.sizeOfPixel * (c - OFFSET * viewPlane.resolution.width + sp.x)
        val y = viewPlane.sizeOfPixel * (r - OFFSET * viewPlane.resolution.height + sp.y)
        val pp = Point2D(x, y)
        val rd = getRayDirection(pp, viewPlane.resolution, viewPlane.sizeOfPixel)
        if (rd.rSquared <= 1) {
            ray = Ray(eye, rd.direction)
        }
        return ray
    }

    override fun getRaySingle(
        r: Int,
        c: Int,
    ): Ray? {
        var ray: Ray? = null
        val x = viewPlane.sizeOfPixel * (c - OFFSET * viewPlane.resolution.width)
        val y = viewPlane.sizeOfPixel * (r - OFFSET * viewPlane.resolution.height)
        val pp = Point2D(x, y)
        val rd = getRayDirection(pp, viewPlane.resolution, viewPlane.sizeOfPixel)
        if (rd.rSquared <= 1) {
            ray = Ray(eye, rd.direction)
        }
        return ray
    }

    private fun getRayDirection(
        pp: Point2D,
        resolution: Resolution,
        s: Double,
    ): RayDirection {
        val x = 2.0 / (s * resolution.width) * pp.x
        val y = 2.0 / (s * resolution.height) * pp.y
        val rSquared = x * x + y * y
        if (rSquared <= 1) {
            val r = sqrt(rSquared)
            val psi = r * maxPsi * MathUtils.PI_ON_180
            val sinPsi = sin(psi)
            val cosPsi = cos(psi)
            // At the exact center r == 0, so x/r and y/r would be NaN. There psi == 0 too, so
            // the in-plane components vanish and the direction is the pure forward axis (-w).
            val sinAlpha = if (r == 0.0) 0.0 else y / r
            val cosAlpha = if (r == 0.0) 0.0 else x / r
            return RayDirection(uvw.pm(sinPsi * cosAlpha, sinPsi * sinAlpha, cosPsi), rSquared)
        } else {
            // Keep rSquared so the caller's `rSquared <= 1` guard rejects out-of-circle pixels.
            return RayDirection(Vector3D.ZERO, rSquared)
        }
    }
}
