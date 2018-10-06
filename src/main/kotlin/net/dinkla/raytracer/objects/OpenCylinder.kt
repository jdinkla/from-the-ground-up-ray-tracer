package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*

/**
 * Created by IntelliJ IDEA.
 * User: jorndinkla
 * Date: 13.04.2010
 * Time: 13:23:24
 * To change this template use File | Settings | File Templates.
 */
class OpenCylinder(y0: Double, y1: Double, private var radius: Double) : GeometricObject() {

    private var y0: Double = 0.0
    internal var y1: Double = 0.0
    private var invRadius: Double = 0.0

    init {
        this.y0 = Math.min(y0, y1)
        this.y1 = Math.max(y0, y1)
        this.invRadius = 1.0 / radius
        boundingBox = calcBoundingBox()
    }

    override fun hit(ray: Ray, sr: Hit): Boolean {

        var t: Double
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
            val e = Math.sqrt(disc)
            val denom = 2.0 * a

            t = (-b - e) / denom    // smaller root
            if (t > MathUtils.K_EPSILON) {
                val yhit = oy + t * dy
                if (yhit > y0 && yhit < y1) {
                    sr.t = t
                    sr.normal = Normal((ox + t * dx) * invRadius, 0.0, (oz + t * dz) * invRadius)
                    // test for hitting from inside
                    if (ray.direction.times(-1.0).dot(sr.normal) < 0.0) {
                        sr.normal = Normal(sr.normal.times(-1.0))
                    }
                    //sr.localHitPoint = ray.linear(tmin.getValue());
                    return true
                }
            }

            t = (-b + e) / denom    // larger root
            if (t > MathUtils.K_EPSILON) {
                val yhit = oy + t * dy
                if (yhit > y0 && yhit < y1) {
                    sr.t = t
                    sr.normal = Normal((ox + t * dx) * invRadius, 0.0, (oz + t * dz) * invRadius)
                    // test for hitting inside surface
                    if (ray.direction.times(-1.0).dot(sr.normal) < 0.0) {
                        sr.normal = Normal(sr.normal.times(-1.0))
                    }
                    //sr.localHitPoint = ray.linear(tmin.getValue());
                    return true
                }
            }
        }

        return false
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        var t: Double
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
            val e = Math.sqrt(disc)
            val denom = 2.0 * a

            t = (-b - e) / denom    // smaller root
            if (t > MathUtils.K_EPSILON) {
                val yhit = oy + t * dy
                if (yhit > y0 && yhit < y1) {
                    tmin.t = t
                    return true
                }
            }

            t = (-b + e) / denom    // larger root
            if (t > MathUtils.K_EPSILON) {
                val yhit = oy + t * dy
                if (yhit > y0 && yhit < y1) {
                    tmin.t = t
                    return true
                }
            }
        }

        return false
    }

    fun calcBoundingBox(): BBox {
        // TODO: better bbox of open cylinder // throw new RuntimeException("OpenCylinder.getBoundingBox");
        val p = Point3D(-radius - MathUtils.K_EPSILON, y0, -radius - MathUtils.K_EPSILON)
        val q = Point3D(radius + MathUtils.K_EPSILON, y1, radius + MathUtils.K_EPSILON)
        return BBox(p, q)
    }

}
