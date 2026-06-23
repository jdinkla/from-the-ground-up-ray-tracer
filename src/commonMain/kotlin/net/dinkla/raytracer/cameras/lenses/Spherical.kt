package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.MathUtils.PI
import net.dinkla.raytracer.math.MathUtils.PI_ON_180
import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.utilities.Resolution
import kotlin.math.cos
import kotlin.math.sin

/**
 * The spherical-panoramic camera from Suffern ch. 11. The view plane is normalized to `[-1, 1]²` and
 * mapped onto a sphere: the horizontal coordinate becomes the azimuth `lambda` (up to [maxLambda]
 * degrees) and the vertical becomes the polar angle `psi` (up to [maxPsi] degrees), so the full plane
 * can cover a 360°×180° panorama. Unlike [FishEye] every pixel maps to a valid ray.
 */
class Spherical(
    viewPlane: ViewPlane,
    eye: Point3D,
    uvw: Basis,
) : AbstractLens(viewPlane, eye, uvw) {
    /** Maximum azimuth in **degrees**; the view plane's width spans `±maxLambda` (180 ⇒ a 360° panorama). */
    var maxLambda: Double = 180.0

    /** Maximum polar angle in **degrees**; the view plane's height spans `±maxPsi` (90 ⇒ a 180° vertical sweep). */
    var maxPsi: Double = 180.0

    override fun getRaySingle(
        r: Int,
        c: Int,
    ): Ray {
        val x = viewPlane.sizeOfPixel * (c - OFFSET * viewPlane.resolution.width)
        val y = viewPlane.sizeOfPixel * (r - OFFSET * viewPlane.resolution.height)
        val pp = Point2D(x, y)
        val direction = getRayDirection(pp, viewPlane.resolution, viewPlane.sizeOfPixel)
        return Ray(eye, direction)
    }

    override fun getRaySampled(
        r: Int,
        c: Int,
        sp: Point2D,
    ): Ray {
        val x = viewPlane.sizeOfPixel * (c - OFFSET * viewPlane.resolution.width + sp.x)
        val y = viewPlane.sizeOfPixel * (r - OFFSET * viewPlane.resolution.height + sp.y)
        val pp = Point2D(x, y)
        val direction = getRayDirection(pp, viewPlane.resolution, viewPlane.sizeOfPixel)
        return Ray(eye, direction)
    }

    /**
     * Maps a view-plane point [pp] (with [resolution] and pixel size [s]) to a world-space direction:
     * the normalized coordinates drive azimuth `lambda` and polar `psi`, which are converted to
     * spherical angles `phi`/`theta` and finally to a unit direction via the camera basis.
     */
    private fun getRayDirection(
        pp: Point2D,
        resolution: Resolution,
        s: Double,
    ): Vector3D {
        val x = 2.0 / (s * resolution.width) * pp.x
        val y = 2.0 / (s * resolution.height) * pp.y

        val lambda = x * maxLambda * PI_ON_180
        val psi = y * maxPsi * PI_ON_180

        val phi = PI - lambda
        val theta = OFFSET * PI - psi

        val sinPhi = sin(phi)
        val cosPhi = cos(phi)

        val sinTheta = sin(theta)
        val cosTheta = cos(theta)

        return uvw.pp(sinTheta * sinPhi, cosTheta, sinTheta * cosPhi)
    }
}
