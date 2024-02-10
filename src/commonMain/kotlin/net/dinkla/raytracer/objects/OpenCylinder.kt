package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.utilities.hash
import kotlin.math.max
import kotlin.math.min
import kotlin.math.sqrt

class OpenCylinder(y0: Double, y1: Double, private var radius: Double) : GeometricObject() {

    private var y0: Double = min(y0, y1)
    internal var y1: Double = max(y0, y1)
    private var invRadius: Double = 1.0 / radius

    init {
        boundingBox = calcBoundingBox()
    }

    override fun hit(ray: Ray, sr: IHit): Boolean {
        val ox = ray.origin.x
        val oy = ray.origin.y
        val oz = ray.origin.z
        val dx = ray.direction.x
        val dy = ray.direction.y
        val dz = ray.direction.z

        val a = dx * dx + dz * dz
        val b = 2.0 * (ox * dx + oz * dz)
        val c = ox * ox + oz * oz - radius * radius
        val disc = b * b - 4.0 * a * c

        if (disc < 0.0) {
            return false
        } else {
            val e = sqrt(disc)
            val denom = 2.0 * a

            val t1 = (-b - e) / denom // smaller root
            if (t1 > MathUtils.K_EPSILON) {
                val yhit = oy + t1 * dy
                if (yhit > y0 && yhit < y1) {
                    sr.t = t1
                    sr.normal = Normal((ox + t1 * dx) * invRadius, 0.0, (oz + t1 * dz) * invRadius)
                    // test for hitting from inside
                    if (ray.direction.times(-1.0).dot(sr.normal) < 0.0) {
                        sr.normal = Normal.create(sr.normal.times(-1.0))
                    }
                    // sr.localHitPoint = ray.linear(tmin.getValue());
                    return true
                }
            }

            val t2 = (-b + e) / denom // larger root
            if (t2 > MathUtils.K_EPSILON) {
                val yhit = oy + t2 * dy
                if (yhit > y0 && yhit < y1) {
                    sr.t = t2
                    sr.normal = Normal((ox + t2 * dx) * invRadius, 0.0, (oz + t2 * dz) * invRadius)
                    // test for hitting inside surface
                    if (ray.direction.times(-1.0).dot(sr.normal) < 0.0) {
                        sr.normal = Normal.create(sr.normal.times(-1.0))
                    }
                    // sr.localHitPoint = ray.linear(tmin.getValue());
                    return true
                }
            }
        }

        return false
    }

    override fun shadowHit(ray: Ray): Shadow {
        val ox = ray.origin.x
        val oy = ray.origin.y
        val oz = ray.origin.z
        val dx = ray.direction.x
        val dy = ray.direction.y
        val dz = ray.direction.z

        val a = dx * dx + dz * dz
        val b = 2.0 * (ox * dx + oz * dz)
        val c = ox * ox + oz * oz - radius * radius
        val disc = b * b - 4.0 * a * c

        if (disc < 0.0) {
            return Shadow.None
        } else {
            val e = sqrt(disc)
            val denom = 2.0 * a
            val t1 = (-b - e) / denom // smaller root
            if (isHit(t1, oy, dy)) return Shadow.Hit(t1)
            val t2 = (-b + e) / denom // larger root
            if (isHit(t2, oy, dy)) return Shadow.Hit(t2)
        }
        return Shadow.None
    }

    private fun isHit(t: Double, oy: Double, dy: Double): Boolean {
        if (t > MathUtils.K_EPSILON) {
            val yHit = oy + t * dy
            if (yHit > y0 && yHit < y1) {
                return true
            }
        }
        return false
    }

    private fun calcBoundingBox(): BBox {
        val p = Point3D(-radius - MathUtils.K_EPSILON, y0, -radius - MathUtils.K_EPSILON)
        val q = Point3D(radius + MathUtils.K_EPSILON, y1, radius + MathUtils.K_EPSILON)
        return BBox(p, q)
    }

    override fun equals(other: Any?): Boolean = this.equals<OpenCylinder>(other) { a, b ->
        a.y0 == b.y0 && a.y1 == b.y1 && a.radius == b.radius
    }

    override fun hashCode(): Int = this.hash(y0, y1, radius)

    override fun toString(): String = "OpenCylinder($y0, $y1, $radius)"
}
