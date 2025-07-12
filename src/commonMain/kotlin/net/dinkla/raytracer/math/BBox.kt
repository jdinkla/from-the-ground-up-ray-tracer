package net.dinkla.raytracer.math

import kotlin.math.max
import kotlin.math.min

data class BBox(
    val p: Point3D = Point3D.ORIGIN,
    val q: Point3D = Point3D.ORIGIN,
) {
    init {
        require(p.x <= q.x && p.y <= q.y && p.z <= q.z) {
            "Invalid bounding box: min point must be <= max point"
        }
    }

    val volume: Double
        get() = (q - p).volume

    fun isInside(r: Point3D): Boolean =
        r.x > p.x && r.x < q.x && r.y > p.y && r.y < q.y && r.z > p.z && r.z < q.z

    class Hit(
        val t0: Double = Double.NaN,
        val t1: Double = Double.NaN,
        val isHit: Boolean = false
    ) {
        constructor(t0: Double, t1: Double) : this(t0, t1, t0 < t1 && t1 > MathUtils.K_EPSILON)
    }

    fun isHit(ray: Ray): Boolean = hit(ray).isHit

    fun hit(ray: Ray): Hit {
        val (txMin, txMax) = calculateAxisIntersection(ray.origin.x, ray.direction.x, p.x, q.x)
        val (tyMin, tyMax) = calculateAxisIntersection(ray.origin.y, ray.direction.y, p.y, q.y)
        val (tzMin, tzMax) = calculateAxisIntersection(ray.origin.z, ray.direction.z, p.z, q.z)

        val t0 = maxOf(txMin, tyMin, tzMin)
        val t1 = minOf(txMax, tyMax, tzMax)

        return Hit(t0, t1)
    }

    private fun calculateAxisIntersection(
        origin: Double,
        direction: Double,
        boxMin: Double,
        boxMax: Double
    ): Pair<Double, Double> {
        return if (direction == 0.0) {
            if (origin < boxMin || origin > boxMax) {
                Double.NEGATIVE_INFINITY to Double.NEGATIVE_INFINITY
            } else {
                Double.NEGATIVE_INFINITY to Double.POSITIVE_INFINITY
            }
        } else {
            val invDir = 1.0 / direction
            val t1 = (boxMin - origin) * invDir
            val t2 = (boxMax - origin) * invDir
            if (invDir >= 0) t1 to t2 else t2 to t1
        }
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
        fun create(v0: Point3D, v1: Point3D, v2: Point3D): BBox {
            val minX = minOf(v0.x, v1.x, v2.x) - MathUtils.K_EPSILON
            val maxX = maxOf(v0.x, v1.x, v2.x) + MathUtils.K_EPSILON
            val minY = minOf(v0.y, v1.y, v2.y) - MathUtils.K_EPSILON
            val maxY = maxOf(v0.y, v1.y, v2.y) + MathUtils.K_EPSILON
            val minZ = minOf(v0.z, v1.z, v2.z) - MathUtils.K_EPSILON
            val maxZ = maxOf(v0.z, v1.z, v2.z) + MathUtils.K_EPSILON

            return BBox(Point3D(minX, minY, minZ), Point3D(maxX, maxY, maxZ))
        }
    }
}
