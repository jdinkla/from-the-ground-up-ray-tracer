package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Ray

abstract class GeometricObject {

    // TODO needed?
    open var isShadows = true

    // TODO open?
    open var material: IMaterial? = null

    // TODO really needed?
    var isInitialized: Boolean = false

    open fun initialize() {
        isInitialized = true
    }

    // TODO open?
    open var boundingBox: BBox = BBox()

    abstract fun hit(ray: Ray, sr: Hit): Boolean

    abstract fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean

}
