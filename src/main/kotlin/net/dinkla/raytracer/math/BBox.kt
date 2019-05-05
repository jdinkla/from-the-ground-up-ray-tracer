package net.dinkla.raytracer.math

import net.dinkla.raytracer.objects.GeometricObject

class BBox(val p: Point3D = Point3D.ORIGIN, val q: Point3D = Point3D.ORIGIN) {

    init {
        assert(p.x <= q.x && p.y <= q.y && p.z <= q.z)
    }

    val volume: Double
        get() = (q - p).volume()

    fun inside(r: Point3D): Boolean {
        val isX = r.x > p.x && r.x < q.x
        val isY = r.y > p.y && r.y < q.y
        val isZ = r.z > p.z && r.z < q.z
        return isX && isY && isZ
    }

    class Hit {
        val t0: Double
        val t1: Double
        val isHit: Boolean

        constructor(t0: Double, t1: Double) {
            this.t0 = t0
            this.t1 = t1
            isHit = t0 < t1 && t1 > MathUtils.K_EPSILON
        }

        constructor() {
            isHit = false
            t0 = java.lang.Double.NaN
            t1 = java.lang.Double.NaN
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
        t0 = if (txMin > tyMin) {
            txMin
        } else {
            tyMin
        }

        if (tzMin > t0) {
            t0 = tzMin
        }

        // find smallest exiting t value
        t1 = if (txMax < tyMax) {
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

        val tx_min: Double
        val ty_min: Double
        val tz_min: Double
        val tx_max: Double
        val ty_max: Double
        val tz_max: Double

        val a = 1.0 / ray.direction.x
        if (a >= 0) {
            tx_min = (p.x - ray.origin.x) * a
            tx_max = (q.x - ray.origin.x) * a
        } else {
            tx_min = (q.x - ray.origin.x) * a
            tx_max = (p.x - ray.origin.x) * a
        }

        val b = 1.0 / ray.direction.y
        if (b >= 0) {
            ty_min = (p.y - ray.origin.y) * b
            ty_max = (q.y - ray.origin.y) * b
        } else {
            ty_min = (q.y - ray.origin.y) * b
            ty_max = (p.y - ray.origin.y) * b
        }

        val c = 1.0 / ray.direction.z
        if (c >= 0) {
            tz_min = (p.z - ray.origin.z) * c
            tz_max = (q.z - ray.origin.z) * c
        } else {
            tz_min = (q.z - ray.origin.z) * c
            tz_max = (p.z - ray.origin.z) * c
        }

        var t0: Double
        var t1: Double

        // find largest entering t value
        t0 = if (tx_min > ty_min) {
            tx_min
        } else {
            ty_min
        }

        if (tz_min > t0) {
            t0 = tz_min
        }

        // find smallest exiting t value
        t1 = if (tx_max < ty_max) {
            tx_max
        } else {
            ty_max
        }
        if (tz_max < t1) {
            t1 = tz_max
        }

        return t0 < t1 && t1 > MathUtils.K_EPSILON
    }

    private fun isContainedIn(bbox: BBox): Boolean {
        val bX = bbox.p.x <= p.x && q.x <= bbox.q.x
        val bY = bbox.p.y <= p.y && q.y <= bbox.q.y
        val bZ = bbox.p.z <= p.z && q.z <= bbox.q.z
        return bX && bY && bZ
    }

    fun clipTo(bbox: BBox): BBox {
        if (isContainedIn(bbox)) {
            return this
        }
        val px = Math.max(p.x, bbox.p.x)
        val py = Math.max(p.y, bbox.p.y)
        val pz = Math.max(p.z, bbox.p.z)

        val qx = Math.min(q.x, bbox.q.x)
        val qy = Math.min(q.y, bbox.q.y)
        val qz = Math.min(q.z, bbox.q.z)

        return BBox(Point3D(px, py, pz), Point3D(qx, qy, qz))
    }

    fun splitLeft(axis: Axis, split: Double): BBox? {
        return when (axis) {
            Axis.X -> BBox(p, Point3D(split, q.y, q.z))
            Axis.Y -> BBox(p, Point3D(q.x, split, q.z))
            Axis.Z -> BBox(p, Point3D(q.x, q.y, split))
        }
    }

    fun splitRight(axis: Axis, split: Double): BBox? {
        return when (axis) {
            Axis.X -> BBox(Point3D(split, p.y, p.z), q)
            Axis.Y -> BBox(Point3D(p.x, split, p.z), q)
            Axis.Z -> BBox(Point3D(p.x, p.y, split), q)
        }
    }

    override fun equals(other: Any?): Boolean {
        return if (other == null || other !is BBox ) {
            false
        } else {
            val o = other as BBox?
            p == o!!.p && q == o.q
        }
    }

    override fun hashCode(): Int = 31 * p.hashCode() + q.hashCode()

    override fun toString(): String {
        return "BBox " + p.toString() + "-" + q.toString()
    }

    companion object {

        fun create(v0: Point3D, v1: Point3D, v2: Point3D): BBox {
            var x0 = java.lang.Double.POSITIVE_INFINITY
            var x1 = java.lang.Double.NEGATIVE_INFINITY
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
            var y0 = java.lang.Double.POSITIVE_INFINITY
            var y1 = java.lang.Double.NEGATIVE_INFINITY
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
            var z0 = java.lang.Double.POSITIVE_INFINITY
            var z1 = java.lang.Double.NEGATIVE_INFINITY
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
            return BBox(Point3D(x0 - MathUtils.K_EPSILON, y0 - MathUtils.K_EPSILON, z0 - MathUtils.K_EPSILON),
                    Point3D(x1 + MathUtils.K_EPSILON, y1 + MathUtils.K_EPSILON, z1 + MathUtils.K_EPSILON))
        }

        fun create(objects: ArrayList<GeometricObject>): BBox {
            return if (objects.size > 0) {
                BBox(PointUtilities.minCoordinates(objects), PointUtilities.maxCoordinates(objects))
            } else {
                BBox()
            }
        }
    }

}
