package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.utilities.Resolution
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

class FishEye(viewPlane: ViewPlane, eye: Point3D, uvw: Basis) : AbstractLens(viewPlane, eye, uvw) {

    private val maxPsi: Double = 1.0

    inner class RayDirection {
        var direction: Vector3D? = null
        var rSquared = 0.0
    }

    override fun getRaySampled(r: Int, c: Int, sp: Point2D): Ray? {
        var ray: Ray? = null
        val x = viewPlane.sizeOfPixel * (c - OFFSET * viewPlane.resolution.hres + sp.x)
        val y = viewPlane.sizeOfPixel * (r - OFFSET * viewPlane.resolution.vres + sp.y)
        val pp = Point2D(x, y)
        val rd = getRayDirection(pp, viewPlane.resolution, viewPlane.sizeOfPixel)
        if (rd.rSquared <= 1) {
            ray = Ray(eye, rd.direction!!)
        }
        return ray
    }

    override fun getRaySingle(r: Int, c: Int): Ray? {
        var ray: Ray? = null
        val x = viewPlane.sizeOfPixel * (c - OFFSET * viewPlane.resolution.hres)
        val y = viewPlane.sizeOfPixel * (r - OFFSET * viewPlane.resolution.vres)
        val pp = Point2D(x, y)
        val rd = getRayDirection(pp, viewPlane.resolution, viewPlane.sizeOfPixel)
        if (rd.rSquared <= 1) {
            ray = Ray(eye, rd.direction!!)
        }
        return ray
    }

    private fun getRayDirection(pp: Point2D, resolution: Resolution, s: Double): RayDirection {
        val rd = RayDirection()
        val x = 2.0 / (s * resolution.hres) * pp.x
        val y = 2.0 / (s * resolution.vres) * pp.y
        val rSquared = x * x + y * y
        if (rSquared <= 1) {
            val r = sqrt(rSquared)
            val psi = r * maxPsi * MathUtils.PI_ON_180
            val sinPsi = sin(psi)
            val cosPsi = cos(psi)
            val sinAlpha = y / r
            val cosAlpha = x / r
            //            rd.direction = uvw.u.minus(sinPsi * cosAlpha).plus(uvw.v.minus(sinPsi * sinAlpha)).minus(uvw.w.minus(cosPsi));
            rd.direction = uvw.pm(sinPsi * cosAlpha, sinPsi * sinAlpha, cosPsi)
            rd.rSquared = rSquared
        } else {
            rd.direction = Vector3D.ZERO
        }
        return rd
    }
}
