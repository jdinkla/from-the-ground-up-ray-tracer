package net.dinkla.raytracer.hits

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.GeometricObject
import net.dinkla.raytracer.objects.compound.Compound

open class Hit : ShadowHit, IHit {

    override var normal: Normal = Normal.ZERO

    // Wird erst ab Compound gefüllt
    var geometricObject: GeometricObject?
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

    constructor(hit: Hit) : super(hit.t) {
        normal = hit.normal
        geometricObject = hit.geometricObject
    }

    fun set(hit: Hit) {
        t = hit.t
        normal = hit.normal
        geometricObject = hit.geometricObject
    }

}
