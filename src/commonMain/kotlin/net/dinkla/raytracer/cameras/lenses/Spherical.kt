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

class Spherical(
    viewPlane: ViewPlane,
    eye: Point3D,
    uvw: Basis,
) : AbstractLens(viewPlane, eye, uvw) {
    private var maxLambda: Double = 180.0
    private var maxPsi: Double = 180.0

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
