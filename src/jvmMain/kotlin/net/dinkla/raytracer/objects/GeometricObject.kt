package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.ShadowHit
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Ray

abstract class GeometricObject : IGeometricObject {

    // TODO needed?
    open var isShadows = true

    override var material: IMaterial? = null

    // TODO really needed?
    var isInitialized: Boolean = false

    open fun initialize() {
        isInitialized = true
    }

    // TODO open?
    open var boundingBox: BBox = BBox()

    abstract fun hit(ray: Ray, sr: IHit): Boolean

    abstract fun shadowHit(ray: Ray, tmin: ShadowHit): Boolean

}
