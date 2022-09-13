package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.Ray

class NullObject : GeometricObject() {
    override fun hit(ray: Ray, sr: Hit): Boolean {
        return false
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        return false
    }
}