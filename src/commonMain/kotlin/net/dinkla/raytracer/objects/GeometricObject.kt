package net.dinkla.raytracer.objects

import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Ray

abstract class GeometricObject : IGeometricObject {

    // TODO needed?
    override var isShadows = true

    override var material: IMaterial? = null

    // TODO really needed?
    var isInitialized: Boolean = false

    override fun initialize() {
        isInitialized = true
    }

    override lateinit var boundingBox: BBox

    abstract override fun hit(ray: Ray, sr: IHit): Boolean
}
