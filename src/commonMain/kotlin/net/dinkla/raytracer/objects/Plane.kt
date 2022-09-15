package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.*

data class Plane(val point: Point3D = Point3D.ORIGIN, val normal: Normal = Normal.UP) : GeometricObject() {

    init {
        boundingBox = BBox(Point3D.MIN, Point3D.MAX)
    }

    override fun hit(ray: Ray, sr: IHit): Boolean {
        val t = ((point - ray.origin) dot normal) / (ray.direction dot normal)
        return if (t > MathUtils.K_EPSILON) {
            sr.t = t
            sr.normal = this.normal
            true
        } else {
            false
        }
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        val t = ((point - ray.origin) dot normal) / (ray.direction dot normal)
        return if (t > MathUtils.K_EPSILON) {
            tmin.t = t
            true
        } else {
            false
        }
    }
}
