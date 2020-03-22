package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D

class Orthographic(viewPlane: ViewPlane) : AbstractLens(viewPlane) {

    private var zw = 1111.0

    override fun getRaySampled(r: Int, c: Int, sp: Point2D): Ray {
        val x = viewPlane.size * (c - OFFSET * (viewPlane.resolution.hres - 1) + sp.x)
        val y = viewPlane.size * (r - OFFSET * (viewPlane.resolution.vres - 1) + sp.y)
        return Ray(Point3D(x, y, zw), Vector3D.BACK)
    }

    override fun getRaySingle(r: Int, c: Int): Ray {
        val x = viewPlane.size * (c - OFFSET * (viewPlane.resolution.hres - 1))
        val y = viewPlane.size * (r - OFFSET * (viewPlane.resolution.vres - 1))
        return Ray(Point3D(x, y, zw), Vector3D.BACK)
    }
}