package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 21.04.2010
 * Time: 20:01:41
 * To change this template use File | Settings | File Templates.
 */
class AlignedBox(val p: Point3D, val q: Point3D) : GeometricObject() {

    override fun hit(ray: Ray, sr: Hit): Boolean {
        val tx_min: Float
        val ty_min: Float
        val tz_min: Float
        val tx_max: Float
        val ty_max: Float
        val tz_max: Float

        val a = 1.0f / ray.d.x
        if (a >= 0) {
            tx_min = (p.x - ray.o.x) * a
            tx_max = (q.x - ray.o.x) * a
        } else {
            tx_min = (q.x - ray.o.x) * a
            tx_max = (p.x - ray.o.x) * a
        }

        val b = 1.0f / ray.d.y
        if (b >= 0) {
            ty_min = (p.y - ray.o.y) * b
            ty_max = (q.y - ray.o.y) * b
        } else {
            ty_min = (q.y - ray.o.y) * b
            ty_max = (p.y - ray.o.y) * b
        }

        val c = 1.0f / ray.d.z
        if (c >= 0) {
            tz_min = (p.z - ray.o.z) * c
            tz_max = (q.z - ray.o.z) * c
        } else {
            tz_min = (q.z - ray.o.z) * c
            tz_max = (p.z - ray.o.z) * c
        }

        var t0: Float
        var t1: Float
        var faceIn: Int
        var faceOut: Int
        // find largest entering t value
        if (tx_min > ty_min) {
            t0 = tx_min
            faceIn = if (a >= 0) 0 else 3
        } else {
            t0 = ty_min
            faceIn = if (b >= 0) 1 else 4
        }
        if (tz_min > t0) {
            t0 = tz_min
            faceIn = if (c >= 0) 2 else 5
        }

        // find smallest exiting t value
        if (tx_max < ty_max) {
            t1 = tx_max
            faceOut = if (a >= 0) 3 else 0
        } else {
            t1 = ty_max
            faceOut = if (b >= 0) 4 else 1
        }
        if (tz_max < t1) {
            t1 = tz_max
            faceOut = if (c >= 0) 5 else 2
        }

        if (t0 < t1 && t1 > MathUtils.K_EPSILON) {
            if (t0 > MathUtils.K_EPSILON) {
                sr.setT(t0)
                sr.normal = getNormal(faceIn)
            } else {
                sr.setT(t1)
                sr.normal = getNormal(faceOut)
            }
            //sr.localHitPoint = ray.linear(tmin.getValue());
            return true
        }
        return false
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        val tx_min: Float
        val ty_min: Float
        val tz_min: Float
        val tx_max: Float
        val ty_max: Float
        val tz_max: Float

        val a = 1.0f / ray.d.x
        if (a >= 0) {
            tx_min = (p.x - ray.o.x) * a
            tx_max = (q.x - ray.o.x) * a
        } else {
            tx_min = (q.x - ray.o.x) * a
            tx_max = (p.x - ray.o.x) * a
        }

        val b = 1.0f / ray.d.y
        if (b >= 0) {
            ty_min = (p.y - ray.o.y) * b
            ty_max = (q.y - ray.o.y) * b
        } else {
            ty_min = (q.y - ray.o.y) * b
            ty_max = (p.y - ray.o.y) * b
        }

        val c = 1.0f / ray.d.z
        if (c >= 0) {
            tz_min = (p.z - ray.o.z) * c
            tz_max = (q.z - ray.o.z) * c
        } else {
            tz_min = (q.z - ray.o.z) * c
            tz_max = (p.z - ray.o.z) * c
        }

        var t0: Float
        var t1: Float
        var faceIn: Int
        var faceOut: Int
        // find largest entering t value
        if (tx_min > ty_min) {
            t0 = tx_min
            faceIn = if (a >= 0) 0 else 3
        } else {
            t0 = ty_min
            faceIn = if (b >= 0) 1 else 4
        }
        if (tz_min > t0) {
            t0 = tz_min
            faceIn = if (c >= 0) 2 else 5
        }

        // find smallest exiting t value
        if (tx_max < ty_max) {
            t1 = tx_max
            faceOut = if (a >= 0) 3 else 0
        } else {
            t1 = ty_max
            faceOut = if (b >= 0) 4 else 1
        }
        if (tz_max < t1) {
            t1 = tz_max
            faceOut = if (c >= 0) 5 else 2
        }

        if (t0 < t1 && t1 > MathUtils.K_EPSILON) {
            if (t0 > MathUtils.K_EPSILON) {
                tmin.setT(t0)
            } else {
                tmin.setT(t1)
            }
            return true
        }
        return false
    }

    internal fun getNormal(face: Int): Normal? {
        when (face) {
            0 -> return Normal(-1f, 0f, 0f)
            1 -> return Normal(0f, -1f, 0f)
            2 -> return Normal(0f, 0f, -1f)
            3 -> return Normal(1f, 0f, 0f)
            4 -> return Normal(0f, 1f, 0f)
            5 -> return Normal(0f, 0f, 1f)
        }
        return null
    }

    override fun getBoundingBox(): BBox {
        return BBox(p, q)
    }

}
