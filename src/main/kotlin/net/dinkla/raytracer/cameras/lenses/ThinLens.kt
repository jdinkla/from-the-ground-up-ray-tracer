package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.cameras.lenses.AbstractLens
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.samplers.Sampler

class ThinLens(viewPlane: ViewPlane) : AbstractLens(viewPlane) {

    var sampler: Sampler? = null           // unit disk

    var lensRadius: Double = 1.0
    var f: Double = 1.0
    var d: Double = 1.0

    override fun getRaySingle(r: Int, c: Int): Ray {
        val x = viewPlane.size * (c - 0.5 * viewPlane.resolution.hres)
        val y = viewPlane.size * (r - 0.5 * viewPlane.resolution.vres)
        return getRay(x, y)
    }

    override fun getRaySampled(r: Int, c: Int, sp: Point2D): Ray {
        val x = viewPlane.size * (c - 0.5 * viewPlane.resolution.hres + sp.x)
        val y = viewPlane.size * (r - 0.5 * viewPlane.resolution.vres + sp.y)
        return getRay(x, y)
    }

    private fun getRay(x: Double, y: Double): Ray {
        val pp = Point2D(x, y)
        val dp = sampler!!.sampleUnitDisk()
        val lp = Point2D(dp.x * lensRadius, dp.y * lensRadius)
        //        Point3D origin = eye.plus(u.minus(lp.x)).plus(v.minus(lp.y));
        // val o = eye!!.plus(uvw!!.pp(lp.x, lp.y, 0.0))
        return Ray(eye!!, getRayDirection(pp, lp))
    }

    private fun getRayDirection(pixel: Point2D, lens: Point2D): Vector3D {
        // val p = Point2D(pixel.x * f / d, pixel.y * f / d)
        //        final Vector3D v1 = u.minus(p.x - lens.x);
        //        final Vector3D v2 = v.minus(p.y - lens.y);
        //        final Vector3D v3 = w.minus(f);
        //final Vector3D dir = v1.plus(v2).minus(v3).normalize();
        return uvw!!.pm(1.0, 1.0, 1.0).normalize()
    }

}
