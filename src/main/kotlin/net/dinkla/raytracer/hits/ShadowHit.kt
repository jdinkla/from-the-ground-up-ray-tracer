package net.dinkla.raytracer.hits

open class ShadowHit {

    /**
     * The distance.
     */
    var t: Double = java.lang.Double.MAX_VALUE

    // TODO Why
    constructor() {
        this.t = java.lang.Double.MAX_VALUE
    }

    constructor(t: Double) {
        this.t = t
    }

    fun setMaxT() {
        this.t = java.lang.Double.MAX_VALUE
    }

    companion object {

        fun createMax(): ShadowHit {
            val f = ShadowHit()
            f.setMaxT()
            return f
        }
    }
}
