package net.dinkla.raytracer.hits

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.IGeometricObject

open class Hit : ShadowHit, IHit {

    override var normal: Normal = Normal.ZERO
    override var geometricObject: IGeometricObject? = null

    constructor() : super()
    constructor(t: Double) : super(t)
    constructor(hit: IHit) : super(hit.t) {
        normal = hit.normal
        geometricObject = hit.geometricObject
    }

    // TODO mutable state
    fun set(hit: Hit) {
        t = hit.t
        normal = hit.normal
        geometricObject = hit.geometricObject
    }
}
