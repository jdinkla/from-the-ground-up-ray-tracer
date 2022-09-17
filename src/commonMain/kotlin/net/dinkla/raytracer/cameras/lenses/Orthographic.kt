package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.*

class Orthographic(viewPlane: ViewPlane, eye: Point3D, uvw: Basis) : AbstractLens(viewPlane, eye, uvw) {

    private var zw = 1111.0

    override fun getRaySampled(r: Int, c: Int, sp: Point2D): Ray {
        val x = viewPlane.sizeOfPixel * (c - OFFSET * (viewPlane.resolution.width - 1) + sp.x)
        val y = viewPlane.sizeOfPixel * (r - OFFSET * (viewPlane.resolution.height - 1) + sp.y)
        return Ray(Point3D(x, y, zw), Vector3D.BACK)
    }

    override fun getRaySingle(r: Int, c: Int): Ray {
        val x = viewPlane.sizeOfPixel * (c - OFFSET * (viewPlane.resolution.width - 1))
        val y = viewPlane.sizeOfPixel * (r - OFFSET * (viewPlane.resolution.height - 1))
        return Ray(Point3D(x, y, zw), Vector3D.BACK)
    }
}