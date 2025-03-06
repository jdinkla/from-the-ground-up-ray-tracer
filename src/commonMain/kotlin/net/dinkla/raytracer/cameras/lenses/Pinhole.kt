package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D

class Pinhole(
    viewPlane: ViewPlane,
    eye: Point3D,
    uvw: Basis,
) : AbstractLens(viewPlane, eye, uvw) {
    var d: Double = 1.0

    override fun getRaySingle(
        r: Int,
        c: Int,
    ): Ray {
        val x = viewPlane.sizeOfPixel * (c - OFFSET * viewPlane.resolution.width)
        val y = viewPlane.sizeOfPixel * (r - OFFSET * viewPlane.resolution.height)
        return Ray(eye, getRayDirection(x, y))
    }

    override fun getRaySampled(
        r: Int,
        c: Int,
        sp: Point2D,
    ): Ray {
        val x = viewPlane.sizeOfPixel * (c - OFFSET * viewPlane.resolution.width + sp.x)
        val y = viewPlane.sizeOfPixel * (r - OFFSET * viewPlane.resolution.height + sp.y)
        return Ray(eye, getRayDirection(x, y))
    }

    private fun getRayDirection(
        x: Double,
        y: Double,
    ): Vector3D = uvw.pm(x, y, d).normalize()
}
