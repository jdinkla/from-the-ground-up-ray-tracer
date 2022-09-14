package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Ray

interface IGeometricObject {
    var isShadows: Boolean
    var boundingBox: BBox
    var material: IMaterial?

    fun initialize()

    fun hit(ray: Ray, sr: IHit): Boolean

    fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean
}
