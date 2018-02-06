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
class OpenCylinder(y0: Float, y1: Float, internal var radius: Float) : GeometricObject() {

    internal var y0: Float = 0.toFloat()
    internal var y1: Float = 0.toFloat()
    internal var invRadius: Float = 0.toFloat()

    init {
        this.y0 = Math.min(y0, y1)
        this.y1 = Math.max(y0, y1)
        this.invRadius = 1.0f / radius
    }

    override fun hit(ray: Ray, sr: Hit): Boolean {

        var t: Float
        val ox = ray.o.x
        val oy = ray.o.y
        val oz = ray.o.z
        val dx = ray.d.x
        val dy = ray.d.y
        val dz = ray.d.z

        val a = dx * dx + dz * dz
        val b = 2.0f * (ox * dx + oz * dz)
        val c = ox * ox + oz * oz - radius * radius
        val disc = b * b - 4.0f * a * c

        if (disc < 0.0f) {
            return false
        } else {
            val e = Math.sqrt(disc.toDouble()).toFloat()
            val denom = 2.0f * a

            t = (-b - e) / denom    // smaller root
            if (t > MathUtils.K_EPSILON) {
                val yhit = oy + t * dy
                if (yhit > y0 && yhit < y1) {
                    sr.setT(t)
                    sr.normal = Normal((ox + t * dx) * invRadius, 0.0f, (oz + t * dz) * invRadius)
                    // test for hitting from inside
                    if (ray.d.mult(-1f).dot(sr.normal) < 0.0) {
                        sr.normal = Normal(sr.normal.mult(-1f))
                    }
                    //sr.localHitPoint = ray.linear(tmin.getValue());
                    return true
                }
            }

            t = (-b + e) / denom    // larger root
            if (t > MathUtils.K_EPSILON) {
                val yhit = oy + t * dy
                if (yhit > y0 && yhit < y1) {
                    sr.setT(t)
                    sr.normal = Normal((ox + t * dx) * invRadius, 0.0f, (oz + t * dz) * invRadius)
                    // test for hitting inside surface
                    if (ray.d.mult(-1f).dot(sr.normal) < 0.0) {
                        sr.normal = Normal(sr.normal.mult(-1f))
                    }
                    //sr.localHitPoint = ray.linear(tmin.getValue());
                    return true
                }
            }
        }

        return false
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        var t: Float
        val ox = ray.o.x
        val oy = ray.o.y
        val oz = ray.o.z
        val dx = ray.d.x
        val dy = ray.d.y
        val dz = ray.d.z

        val a = dx * dx + dz * dz
        val b = 2.0f * (ox * dx + oz * dz)
        val c = ox * ox + oz * oz - radius * radius
        val disc = b * b - 4.0f * a * c

        if (disc < 0.0f) {
            return false
        } else {
            val e = Math.sqrt(disc.toDouble()).toFloat()
            val denom = 2.0f * a

            t = (-b - e) / denom    // smaller root
            if (t > MathUtils.K_EPSILON) {
                val yhit = oy + t * dy
                if (yhit > y0 && yhit < y1) {
                    tmin.setT(t)
                    return true
                }
            }

            t = (-b + e) / denom    // larger root
            if (t > MathUtils.K_EPSILON) {
                val yhit = oy + t * dy
                if (yhit > y0 && yhit < y1) {
                    tmin.setT(t)
                    return true
                }
            }
        }

        return false
    }

    override fun getBoundingBox(): BBox {
        // TODO: better bbox of open cylinder // throw new RuntimeException("OpenCylinder.getBoundingBox");
        val p = Point3D(-radius - MathUtils.K_EPSILON, y0, -radius - MathUtils.K_EPSILON)
        val q = Point3D(radius + MathUtils.K_EPSILON, y1, radius + MathUtils.K_EPSILON)
        return BBox(p, q)
    }

}
