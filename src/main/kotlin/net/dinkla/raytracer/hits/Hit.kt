package net.dinkla.raytracer.hits

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.compound.Compound
import net.dinkla.raytracer.objects.GeometricObject

open class Hit : ShadowHit {

    var normal: Normal = Normal.ZERO

    // Wird erst ab Compound gefüllt
    var `object`: GeometricObject?
        set(`object`: GeometricObject?) {
            assert(`object` !is Compound)
            field = `object`
        }

    constructor() : super() {
        normal = Normal.ZERO
        `object` = null
    }

    constructor(t: Double) : super(t) {
        normal = Normal.ZERO
        `object` = null
    }

    constructor(hit: Hit) : super(hit.t) {
        normal = hit.normal
        `object` = hit.`object`
    }

    fun set(hit: Hit) {
        t = hit.t
        normal = hit.normal
        `object` = hit.`object`
    }

}
