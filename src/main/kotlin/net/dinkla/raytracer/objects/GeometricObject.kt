package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.Hit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.materials.Material
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Ray

open abstract class GeometricObject {

    open var isShadows = true

    open var material: Material? = null

    var isInitialized: Boolean = false

    open var boundingBox: BBox = BBox()

    init {
        isInitialized = false
    }

    abstract fun hit(ray: Ray, sr: Hit): Boolean

    abstract fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean

    open fun initialize() {
        isInitialized = true
    }
}
