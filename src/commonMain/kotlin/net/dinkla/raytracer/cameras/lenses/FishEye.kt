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

/**
 * The fisheye camera from Suffern ch. 11. The view plane is normalized to the unit square and only
 * pixels inside the unit image circle (`r² <= 1`) map to a ray; pixels outside it map to no ray
 * (the lens returns `null`), producing the characteristic circular image.
 *
 * For an in-circle pixel the radius `r` is turned into a polar angle `psi = r * maxPsi` (the field of
 * view) and an azimuth `alpha`, and the ray direction is `sin(psi)cos(alpha)*u + sin(psi)sin(alpha)*v
 * - cos(psi)*w` in camera space (`-w` is the forward axis under `Basis.pm`).
 */
class FishEye(
    viewPlane: ViewPlane,
    eye: Point3D,
    uvw: Basis,
) : AbstractLens(viewPlane, eye, uvw) {
    /** Field of view as a half-angle in **degrees** (`psi = r * maxPsi`); larger values widen the fisheye. */
    var maxPsi: Double = 1.0

    /** A computed ray [direction] together with the squared image radius [rSquared] used to gate it. */
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

    /**
     * Maps a view-plane point [pp] (with [resolution] and pixel size [s]) to a [RayDirection]. The
     * point is normalized to `[-1, 1]²`; when its radius `r² <= 1` it is mapped through the fisheye
     * projection, otherwise [Vector3D.ZERO] is returned with `rSquared` preserved so the caller's
     * `rSquared <= 1` guard rejects the pixel.
     */
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
