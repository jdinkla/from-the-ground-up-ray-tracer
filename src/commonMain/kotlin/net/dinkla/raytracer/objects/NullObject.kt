package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.Shadow
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Ray

class NullObject : GeometricObject() {
    init {
        boundingBox = BBox()
    }

    override fun hit(
        ray: Ray,
        sr: IHit,
    ): Boolean = false

    override fun shadowHit(ray: Ray): Shadow = Shadow.None
}
