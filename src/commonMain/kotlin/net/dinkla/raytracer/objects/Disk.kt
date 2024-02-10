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

open class Disk(val center: Point3D, val radius: Double, val normal: Normal) : GeometricObject() {

    init {
        boundingBox = BBox(center - radius, center + radius)
    }

    override fun hit(ray: Ray, sr: IHit): Boolean {
        val nom = (center - ray.origin) dot normal
        val denom = ray.direction dot normal
        val t = nom / denom
        if (t <= MathUtils.K_EPSILON) {
            return false
        }
        val p = ray.linear(t)
        return if (center.sqrDistance(p) < radius * radius) {
            sr.t = t
            sr.normal = normal
            //            sr.localHitPoint = p;
            true
        } else {
            false
        }
    }

    override fun shadowHit(ray: Ray): Shadow {
        val t = ((center - ray.origin) dot normal) / (ray.direction dot normal)
        return when {
            t <= MathUtils.K_EPSILON -> Shadow.None
            center.sqrDistance(ray.linear(t)) < radius * radius -> Shadow.Hit(t)
            else -> Shadow.None
        }
    }

    fun getNormal(p: Point3D): Normal = normal

    override fun equals(other: Any?): Boolean = this.equals<Disk>(other) { a, b ->
        a.center == b.center && a.radius == b.radius && a.normal == b.normal
    }

    override fun hashCode(): Int = hash(center, radius, normal)

    override fun toString(): String = "Disk($center, $radius, $normal)"
}
