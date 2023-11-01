package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.utilities.hash
import kotlin.math.sqrt

class Sphere(val center: Point3D = Point3D.ORIGIN, val radius: Double = 0.0) : GeometricObject() {

    init {
        boundingBox = BBox(center - radius, center + radius)
    }

    constructor(center: Point3D, radius: Double, material: IMaterial) : this(center, radius) {
        this.material = material
    }

    override fun hit(ray: Ray, sr: IHit): Boolean {
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

    override fun shadowHit(ray: Ray): Shadow {
        val temp = ray.origin - center
        val a = ray.direction dot ray.direction
        val b = temp * 2.0 dot ray.direction
        val c = (temp dot temp) - radius * radius
        val disc = b * b - 4.0 * a * c

        if (disc < 0) {
            return Shadow.None
        } else {
            val e = sqrt(disc)
            val denom = 2 * a
            val t = (-b - e) / denom
            if (t > MathUtils.K_EPSILON) {
                return Shadow.Hit(t)
            }
            val t2 = (-b + e) / denom
            if (t2 > MathUtils.K_EPSILON) {
                return Shadow.Hit(t2)
            }
        }
        return Shadow.None
    }

    override fun equals(other: Any?): Boolean = this.equals<Sphere>(other) { a, b ->
        a.center == b.center && a.radius == b.radius && a.material == b.material
    }

    override fun hashCode(): Int = this.hash(center, radius)

    override fun toString(): String = "Sphere($center, $radius)"
}
