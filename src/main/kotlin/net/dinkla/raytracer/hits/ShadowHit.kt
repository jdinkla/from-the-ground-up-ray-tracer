package net.dinkla.raytracer.hits

open class ShadowHit {

    var t: Double = java.lang.Double.MAX_VALUE

    // TODO Why
    constructor() {
        this.t = java.lang.Double.MAX_VALUE
    }

    constructor(t: Double) {
        this.t = t
    }

    // TODO remove if not needed if all groovy removed
//    fun setMaxT() {
//        this.t = java.lang.Double.MAX_VALUE
//    }

    companion object {

        // TODO remove if not needed if all groovy removed
//        fun createMax(): ShadowHit {
//            val f = ShadowHit()
//            f.setMaxT()
//            return f
//        }
    }
}
