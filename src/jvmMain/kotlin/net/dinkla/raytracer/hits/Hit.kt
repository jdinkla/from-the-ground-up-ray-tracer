package net.dinkla.raytracer.hits

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.objects.compound.Compound

open class Hit : ShadowHit, IHit {

    override var normal: Normal = Normal.ZERO

    // Wird erst ab Compound gefüllt
    override var geometricObject: IGeometricObject?
        set(value) {
            assert(value !is Compound)
            field = value
        }

    constructor() : super() {
        normal = Normal.ZERO
        geometricObject = null
    }

    constructor(t: Double) : super(t) {
        normal = Normal.ZERO
        geometricObject = null
    }

    constructor(hit: IHit) : super(hit.t) {
        normal = hit.normal
        geometricObject = hit.geometricObject
    }

    fun set(hit: Hit) {
        t = hit.t
        normal = hit.normal
        geometricObject = hit.geometricObject
    }

}
