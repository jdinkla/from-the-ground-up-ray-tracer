package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.MathUtils
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray

data class Plane(
    val point: Point3D = Point3D.ORIGIN,
    val normal: Normal = Normal.UP,
) : GeometricObject() {
    init {
        boundingBox = BBox(Point3D.MIN, Point3D.MAX)
    }

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean {
        val t = ((point - ray.origin) dot normal) / (ray.direction dot normal)
        return if (t > MathUtils.K_EPSILON) {
            sr.t = t
            sr.normal = this.normal
            true
        } else {
            false
        }
    }

    override fun shadowHit(ray: Ray): Shadow {
        val t = ((point - ray.origin) dot normal) / (ray.direction dot normal)
        return if (t > MathUtils.K_EPSILON) {
            Shadow.Hit(t)
        } else {
            Shadow.None
        }
    }
}
