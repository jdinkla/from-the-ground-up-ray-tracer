package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Face
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray

data class AlignedBox(
    val p: Point3D,
    val q: Point3D,
) : GeometricObject() {
    init {
        boundingBox = BBox(p, q)
    }

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean {
        val (txMin, txMax, a) = minAndMax(ray.direction.x, ray.origin.x, p.x, q.x)
        val (tyMin, tyMax, b) = minAndMax(ray.direction.y, ray.origin.y, p.y, q.y)
        val (tzMin, tzMax, c) = minAndMax(ray.direction.z, ray.origin.z, p.z, q.z)

        var t0: Double // largest entering t value
        var faceIn: Face
        if (txMin > tyMin) {
            t0 = txMin
            faceIn = if (a >= 0) Face.LEFT else Face.RIGHT
        } else {
            t0 = tyMin
            faceIn = if (b >= 0) Face.BOTTOM else Face.TOP
        }
        if (tzMin > t0) {
            t0 = tzMin
            faceIn = if (c >= 0) Face.FRONT else Face.BACK
        }

        var t1: Double // find smallest exiting t value
        var faceOut: Face
        if (txMax < tyMax) {
            t1 = txMax
            faceOut = if (a >= 0) Face.RIGHT else Face.LEFT
        } else {
            t1 = tyMax
            faceOut = if (b >= 0) Face.TOP else Face.BOTTOM
        }
        if (tzMax < t1) {
            t1 = tzMax
            faceOut = if (c >= 0) Face.BACK else Face.FRONT
        }

        if (t0 < t1 && t1 > MathUtils.K_EPSILON) {
            if (t0 > MathUtils.K_EPSILON) {
                sr.t = t0
                sr.normal = faceIn.normal
            } else {
                sr.t = t1
                sr.normal = faceOut.normal
            }
            // sr.localHitPoint = ray.linear(tmin.getValue());
            return true
        }
        return false
    }

    override fun shadowHit(ray: Ray): Shadow {
        val (txMin, txMax, _) = minAndMax(ray.direction.x, ray.origin.x, p.x, q.x)
        val (tyMin, tyMax, _) = minAndMax(ray.direction.y, ray.origin.y, p.y, q.y)
        val (tzMin, tzMax, _) = minAndMax(ray.direction.z, ray.origin.z, p.z, q.z)

        // find largest entering t value
        var t0 =
            if (txMin > tyMin) {
                txMin
            } else {
                tyMin
            }
        if (tzMin > t0) {
            t0 = tzMin
        }

        // find smallest exiting t value
        var t1 =
            if (txMax < tyMax) {
                txMax
            } else {
                tyMax
            }
        if (tzMax < t1) {
            t1 = tzMax
        }

        if (t0 < t1 && t1 > MathUtils.K_EPSILON) {
            return if (t0 > MathUtils.K_EPSILON) {
                Shadow.Hit(t0)
            } else {
                Shadow.Hit(t1)
            }
        }
        return Shadow.None
    }

    private fun minAndMax(
        direction: Double,
        origin: Double,
        p: Double,
        q: Double,
    ): Triple<Double, Double, Double> {
        val tMin: Double
        val tMax: Double
        val a = 1.0 / direction
        if (a >= 0) {
            tMin = (p - origin) * a
            tMax = (q - origin) * a
        } else {
            tMin = (q - origin) * a
            tMax = (p - origin) * a
        }
        return Triple(tMin, tMax, a)
    }
}
