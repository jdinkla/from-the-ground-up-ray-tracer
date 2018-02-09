package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.cameras.lenses.AbstractLens
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.utilities.Resolution

class FishEye(viewPlane: ViewPlane) : AbstractLens(viewPlane) {

    var maxPsi: Double = 0.toDouble()

    inner class RayDirection {
        var direction: Vector3D? = null
        var rSquared = 0.0
    }

    init {
        maxPsi = 1.0
    }

    override fun getRaySampled(r: Int, c: Int, sp: Point2D): Ray? {
        var ray: Ray? = null
        val x = viewPlane!!.size * (c - 0.5 * viewPlane!!.resolution.hres + sp.x)
        val y = viewPlane!!.size * (r - 0.5 * viewPlane!!.resolution.vres + sp.y)
        val pp = Point2D(x, y)
        val rd = getRayDirection(pp, viewPlane!!.resolution, viewPlane!!.size)
        if (rd.rSquared <= 1) {
            ray = Ray(eye!!, rd.direction!!)
        }
        return ray
    }

    override fun getRaySingle(r: Int, c: Int): Ray? {
        var ray: Ray? = null
        val x = viewPlane!!.size * (c - 0.5 * viewPlane!!.resolution.hres)
        val y = viewPlane!!.size * (r - 0.5 * viewPlane!!.resolution.vres)
        val pp = Point2D(x, y)
        val rd = getRayDirection(pp, viewPlane!!.resolution, viewPlane!!.size)
        if (rd.rSquared <= 1) {
            ray = Ray(eye!!, rd.direction!!)
        }
        return ray
    }

    protected fun getRayDirection(pp: Point2D, resolution: Resolution, s: Double): RayDirection {
        val rd = RayDirection()
        val x = 2.0 / (s * resolution.hres) * pp.x
        val y = 2.0 / (s * resolution.vres) * pp.y
        val rSquared = x * x + y * y
        if (rSquared <= 1) {
            val r = Math.sqrt(rSquared)
            val psi = r * maxPsi * MathUtils.PI_ON_180
            val sinPsi = Math.sin(psi)
            val cosPsi = Math.cos(psi)
            val sinAlpha = y / r
            val cosAlpha = x / r
            //            rd.direction = uvw.u.mult(sinPsi * cosAlpha).plus(uvw.v.mult(sinPsi * sinAlpha)).minus(uvw.w.mult(cosPsi));
            rd.direction = uvw!!.pm(sinPsi * cosAlpha, sinPsi * sinAlpha, cosPsi)
            rd.rSquared = rSquared
        } else {
            rd.direction = Vector3D.ZERO
        }
        return rd

    }
}
