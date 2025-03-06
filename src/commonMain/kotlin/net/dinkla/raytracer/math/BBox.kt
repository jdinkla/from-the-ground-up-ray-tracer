package net.dinkla.raytracer.math

import kotlin.math.max
import kotlin.math.min

data class BBox(
    val p: Point3D = Point3D.ORIGIN,
    val q: Point3D = Point3D.ORIGIN,
) {
    init {
        if (!(p.x <= q.x && p.y <= q.y && p.z <= q.z)) {
            throw AssertionError()
        }
    }

    val volume: Double
        get() = (q - p).volume

    fun isInside(r: Point3D): Boolean {
        val isX = r.x > p.x && r.x < q.x
        val isY = r.y > p.y && r.y < q.y
        val isZ = r.z > p.z && r.z < q.z
        return isX && isY && isZ
    }

    class Hit {
        private val t0: Double
        val t1: Double
        val isHit: Boolean

        constructor(t0: Double, t1: Double) {
            this.t0 = t0
            this.t1 = t1
            isHit = t0 < t1 && t1 > MathUtils.K_EPSILON
        }

        constructor() {
            isHit = false
            t0 = Double.NaN
            t1 = Double.NaN
        }
    }

    fun hitX(ray: Ray): Hit {
        val txMin: Double
        val tyMin: Double
        val tzMin: Double
        val txMax: Double
        val tyMax: Double
        val tzMax: Double

        val a = 1.0 / ray.direction.x
        if (a >= 0) {
            txMin = (p.x - ray.origin.x) * a
            txMax = (q.x - ray.origin.x) * a
        } else {
            txMin = (q.x - ray.origin.x) * a
            txMax = (p.x - ray.origin.x) * a
        }

        val b = 1.0 / ray.direction.y
        if (b >= 0) {
            tyMin = (p.y - ray.origin.y) * b
            tyMax = (q.y - ray.origin.y) * b
        } else {
            tyMin = (q.y - ray.origin.y) * b
            tyMax = (p.y - ray.origin.y) * b
        }

        val c = 1.0 / ray.direction.z
        if (c >= 0) {
            tzMin = (p.z - ray.origin.z) * c
            tzMax = (q.z - ray.origin.z) * c
        } else {
            tzMin = (q.z - ray.origin.z) * c
            tzMax = (p.z - ray.origin.z) * c
        }

        var t0: Double
        var t1: Double

        // find largest entering t value
        t0 =
            if (txMin > tyMin) {
                txMin
            } else {
                tyMin
            }

        if (tzMin > t0) {
            t0 = tzMin
        }

        // find smallest exiting t value
        t1 =
            if (txMax < tyMax) {
                txMax
            } else {
                tyMax
            }
        if (tzMax < t1) {
            t1 = tzMax
        }

        return Hit(t0, t1)
    }

    fun hit(ray: Ray): Boolean {
        val txMin: Double
        val tyMin: Double
        val tzMin: Double
        val txMax: Double
        val tyMax: Double
        val tzMax: Double

        val a = 1.0 / ray.direction.x
        if (a >= 0) {
            txMin = (p.x - ray.origin.x) * a
            txMax = (q.x - ray.origin.x) * a
        } else {
            txMin = (q.x - ray.origin.x) * a
            txMax = (p.x - ray.origin.x) * a
        }

        val b = 1.0 / ray.direction.y
        if (b >= 0) {
            tyMin = (p.y - ray.origin.y) * b
            tyMax = (q.y - ray.origin.y) * b
        } else {
            tyMin = (q.y - ray.origin.y) * b
            tyMax = (p.y - ray.origin.y) * b
        }

        val c = 1.0 / ray.direction.z
        if (c >= 0) {
            tzMin = (p.z - ray.origin.z) * c
            tzMax = (q.z - ray.origin.z) * c
        } else {
            tzMin = (q.z - ray.origin.z) * c
            tzMax = (p.z - ray.origin.z) * c
        }

        var t0: Double
        var t1: Double

        // find largest entering t value
        t0 =
            if (txMin > tyMin) {
                txMin
            } else {
                tyMin
            }

        if (tzMin > t0) {
            t0 = tzMin
        }

        // find smallest exiting t value
        t1 =
            if (txMax < tyMax) {
                txMax
            } else {
                tyMax
            }
        if (tzMax < t1) {
            t1 = tzMax
        }

        return t0 < t1 && t1 > MathUtils.K_EPSILON
    }

    private fun isContainedIn(bBox: BBox): Boolean =
        bBox.p.x <= p.x &&
            q.x <= bBox.q.x &&
            bBox.p.y <= p.y &&
            q.y <= bBox.q.y &&
            bBox.p.z <= p.z &&
            q.z <= bBox.q.z

    fun clipTo(bBox: BBox): BBox {
        if (isContainedIn(bBox)) {
            return this
        }
        val px = max(p.x, bBox.p.x)
        val py = max(p.y, bBox.p.y)
        val pz = max(p.z, bBox.p.z)

        val qx = min(q.x, bBox.q.x)
        val qy = min(q.y, bBox.q.y)
        val qz = min(q.z, bBox.q.z)

        return BBox(Point3D(px, py, pz), Point3D(qx, qy, qz))
    }

    fun splitLeft(
        axis: Axis,
        split: Double,
    ): BBox =
        when (axis) {
            Axis.X -> BBox(p, Point3D(split, q.y, q.z))
            Axis.Y -> BBox(p, Point3D(q.x, split, q.z))
            Axis.Z -> BBox(p, Point3D(q.x, q.y, split))
        }

    fun splitRight(
        axis: Axis,
        split: Double,
    ): BBox =
        when (axis) {
            Axis.X -> BBox(Point3D(split, p.y, p.z), q)
            Axis.Y -> BBox(Point3D(p.x, split, p.z), q)
            Axis.Z -> BBox(Point3D(p.x, p.y, split), q)
        }

    companion object {
        fun create(
            v0: Point3D,
            v1: Point3D,
            v2: Point3D,
        ): BBox {
            var x0 = Double.POSITIVE_INFINITY
            var x1 = Double.NEGATIVE_INFINITY
            if (v0.x < x0) {
                x0 = v0.x
            }
            if (v1.x < x0) {
                x0 = v1.x
            }
            if (v2.x < x0) {
                x0 = v2.x
            }
            if (v0.x > x1) {
                x1 = v0.x
            }
            if (v1.x > x1) {
                x1 = v1.x
            }
            if (v2.x > x1) {
                x1 = v2.x
            }
            var y0 = Double.POSITIVE_INFINITY
            var y1 = Double.NEGATIVE_INFINITY
            if (v0.y < y0) {
                y0 = v0.y
            }
            if (v1.y < y0) {
                y0 = v1.y
            }
            if (v2.y < y0) {
                y0 = v2.y
            }
            if (v0.y > y1) {
                y1 = v0.y
            }
            if (v1.y > y1) {
                y1 = v1.y
            }
            if (v2.y > y1) {
                y1 = v2.y
            }
            var z0 = Double.POSITIVE_INFINITY
            var z1 = Double.NEGATIVE_INFINITY
            if (v0.z < z0) {
                z0 = v0.z
            }
            if (v1.z < z0) {
                z0 = v1.z
            }
            if (v2.z < z0) {
                z0 = v2.z
            }
            if (v0.z > z1) {
                z1 = v0.z
            }
            if (v1.z > z1) {
                z1 = v1.z
            }
            if (v2.z > z1) {
                z1 = v2.z
            }
            return BBox(
                Point3D(x0 - MathUtils.K_EPSILON, y0 - MathUtils.K_EPSILON, z0 - MathUtils.K_EPSILON),
                Point3D(x1 + MathUtils.K_EPSILON, y1 + MathUtils.K_EPSILON, z1 + MathUtils.K_EPSILON),
            )
        }
    }
}
