package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.interfaces.hash
import net.dinkla.raytracer.materials.IMaterial
import kotlin.math.sqrt

class Sphere(val center: Point3D = Point3D.ORIGIN, val radius: Double = 0.0) : GeometricObject() {

    init {
        boundingBox = BBox(center - radius, center + radius)
    }

    constructor(center: Point3D, radius: Double, material: IMaterial) : this(center, radius) {
        this.material = material
    }

    override fun hit(ray: Ray, sr: Hit): Boolean {
        val temp = ray.origin - center
        val a = ray.direction dot ray.direction
        val b = (temp * 2.0) dot ray.direction
        val c = (temp dot temp) - radius * radius
        val disc = b * b - 4.0 * a * c

        if (disc < 0) {
            return false
        } else {
            val e = sqrt(disc)
            val denom = 2 * a
            var t = (-b - e) / denom
            if (t > MathUtils.K_EPSILON) {
                sr.t = t
                sr.normal = Normal.create((ray.direction * t + temp) * (1.0 / radius))
                return true
            }
            t = (-b + e) / denom
            if (t > MathUtils.K_EPSILON) {
                sr.t = t
                sr.normal = Normal.create(((ray.direction * t) + temp) * (1.0 / radius))
                return true
            }
        }
        return false
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        val temp = ray.origin - center
        val a = ray.direction dot ray.direction
        val b = temp * 2.0 dot ray.direction
        val c = (temp dot temp) - radius * radius
        val disc = b * b - 4.0 * a * c

        if (disc < 0) {
            return false
        } else {
            val e = sqrt(disc)
            val denom = 2 * a
            var t = (-b - e) / denom
            if (t > MathUtils.K_EPSILON) {
                tmin.t = t
                return true
            }
            t = (-b + e) / denom
            if (t > MathUtils.K_EPSILON) {
                tmin.t = t
                return true
            }
        }
        return false
    }

    override fun equals(other: Any?): Boolean = this.equals<Sphere>(other) { a, b ->
        a.center == b.center && a.radius == b.radius && a.material == b.material
    }

    override fun hashCode(): Int = this.hash(center, radius)

    override fun toString(): String = "Sphere($center, $radius)"

}
