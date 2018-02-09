package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.cameras.lenses.AbstractLens
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.utilities.Resolution

class Spherical(viewPlane: ViewPlane) : AbstractLens(viewPlane) {

    var maxLambda: Double = 0.toDouble()
    var maxPsi: Double = 0.toDouble()

    init {
        maxLambda = 180.0
        maxPsi = 180.0
    }

    override fun getRaySingle(r: Int, c: Int): Ray {
        val x = viewPlane!!.size * (c - 0.5 * viewPlane!!.resolution.hres)
        val y = viewPlane!!.size * (r - 0.5 * viewPlane!!.resolution.vres)
        val pp = Point2D(x, y)
        val direction = getRayDirection(pp, viewPlane!!.resolution, viewPlane!!.size)
        return Ray(eye!!, direction)
    }

    override fun getRaySampled(r: Int, c: Int, sp: Point2D): Ray {
        val x = viewPlane!!.size * (c - 0.5 * viewPlane!!.resolution.hres + sp.x)
        val y = viewPlane!!.size * (r - 0.5 * viewPlane!!.resolution.vres + sp.y)
        val pp = Point2D(x, y)
        val direction = getRayDirection(pp, viewPlane!!.resolution, viewPlane!!.size)
        return Ray(eye!!, direction)
    }

    protected fun getRayDirection(pp: Point2D, resolution: Resolution, s: Double): Vector3D {
        val x = 2.0 / (s * resolution.hres) * pp.x
        val y = 2.0 / (s * resolution.vres) * pp.y

        val lambda = x * maxLambda * MathUtils.PI_ON_180
        val psi = y * maxPsi * MathUtils.PI_ON_180

        val phi = Math.PI - lambda
        val theta = 0.5 * Math.PI - psi

        val sinPhi = Math.sin(phi)
        val cosPhi = Math.cos(phi)

        val sinTheta = Math.sin(theta)
        val cosTheta = Math.cos(theta)

        //        Vector3D direction = u.mult(sinTheta * sinPhi).plus(v.mult(cosTheta)).plus(w.mult(sinTheta * cosPhi));
        return uvw!!.pp(sinTheta * sinPhi, cosTheta, sinTheta * cosPhi)
    }
}
