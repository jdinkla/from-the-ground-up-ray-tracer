package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.utilities.Resolution

class Spherical(viewPlane: ViewPlane) : AbstractLens(viewPlane) {

    var maxLambda: Double = 180.0
    var maxPsi: Double = 180.0

    override fun getRaySingle(r: Int, c: Int): Ray {
        val x = viewPlane.size * (c - OFFSET * viewPlane.resolution.hres)
        val y = viewPlane.size * (r - OFFSET * viewPlane.resolution.vres)
        val pp = Point2D(x, y)
        val direction = getRayDirection(pp, viewPlane.resolution, viewPlane.size)
        return Ray(eye!!, direction)
    }

    override fun getRaySampled(r: Int, c: Int, sp: Point2D): Ray {
        val x = viewPlane.size * (c - OFFSET * viewPlane.resolution.hres + sp.x)
        val y = viewPlane.size * (r - OFFSET * viewPlane.resolution.vres + sp.y)
        val pp = Point2D(x, y)
        val direction = getRayDirection(pp, viewPlane.resolution, viewPlane.size)
        return Ray(eye!!, direction)
    }

    private fun getRayDirection(pp: Point2D, resolution: Resolution, s: Double): Vector3D {
        val x = 2.0 / (s * resolution.hres) * pp.x
        val y = 2.0 / (s * resolution.vres) * pp.y

        val lambda = x * maxLambda * MathUtils.PI_ON_180
        val psi = y * maxPsi * MathUtils.PI_ON_180

        val phi = Math.PI - lambda
        val theta = OFFSET * Math.PI - psi

        val sinPhi = Math.sin(phi)
        val cosPhi = Math.cos(phi)

        val sinTheta = Math.sin(theta)
        val cosTheta = Math.cos(theta)

        //        Vector3D direction = u.minus(sinTheta * sinPhi).plus(v.minus(cosTheta)).plus(w.minus(sinTheta * cosPhi));
        return uvw!!.pp(sinTheta * sinPhi, cosTheta, sinTheta * cosPhi)
    }
}
