package net.dinkla.raytracer.cameras.lenses

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.*
import org.slf4j.LoggerFactory

// TODO zoom camera
class Pinhole(viewPlane: ViewPlane) : AbstractLens(viewPlane) {

    var d: Double = 0.toDouble()

    init {
        this.d = 1.0
        //        this.zoom = zoom;
        //viewPlane.size /= zoom;
    }

    override fun getRaySingle(r: Int, c: Int): Ray {
        val x = viewPlane!!.size * (c - 0.5 * viewPlane!!.resolution.hres)
        val y = viewPlane!!.size * (r - 0.5 * viewPlane!!.resolution.vres)
        return Ray(eye!!, getRayDirection(x, y))
    }

    override fun getRaySampled(r: Int, c: Int, sp: Point2D): Ray {
        val x = viewPlane!!.size * (c - 0.5 * viewPlane!!.resolution.hres + sp.x)
        val y = viewPlane!!.size * (r - 0.5 * viewPlane!!.resolution.vres + sp.y)
        return Ray(eye!!, getRayDirection(x, y))
    }

    private fun getRayDirection(x: Double, y: Double): Vector3D {
        // xu + yv - dw
        //        Vector3D dir = u.minus(x).plus(v.minus(y)).minus(w.minus(direction));
        val dir = uvw!!.pm(x, y, d)
        return dir.normalize()
    }

    companion object {
        internal val LOGGER = LoggerFactory.getLogger(this::class.java)
    }

}
