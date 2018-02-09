package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.cameras.lenses.AbstractLens
import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.math.Vector3D.Companion

class Orthographic(viewPlane: ViewPlane) : AbstractLens(viewPlane) {

    var zw: Double = 0.toDouble()

    init {
        this.zw = 1111.0
    }

    override fun getRaySampled(r: Int, c: Int, sp: Point2D): Ray {
        val x = (viewPlane!!.size * (c - 0.5 * (viewPlane!!.resolution.hres - 1) + sp.x)).toInt()
        val y = (viewPlane!!.size * (r - 0.5 * (viewPlane!!.resolution.vres - 1) + sp.y)).toInt()
        return Ray(Point3D(x.toDouble(), y.toDouble(), zw), Vector3D.BACK)
    }

    override fun getRaySingle(r: Int, c: Int): Ray {
        val x = (viewPlane!!.size * (c - 0.5 * (viewPlane!!.resolution.hres - 1))).toInt()
        val y = (viewPlane!!.size * (r - 0.5 * (viewPlane!!.resolution.vres - 1))).toInt()
        return Ray(Point3D(x.toDouble(), y.toDouble(), zw), Vector3D.BACK)
    }
}