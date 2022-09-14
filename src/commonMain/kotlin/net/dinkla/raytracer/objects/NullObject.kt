package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.math.Ray

class NullObject : GeometricObject() {
    override fun hit(ray: Ray, sr: IHit): Boolean {
        return false
    }

    override fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean {
        return false
    }
}