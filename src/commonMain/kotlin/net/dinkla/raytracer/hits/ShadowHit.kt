package net.dinkla.raytracer.hits

open class ShadowHit(var t: Double = Double.MAX_VALUE)

sealed interface Shadow {
    fun isHit(): Boolean
    data object None : Shadow {
        override fun isHit() = false
    }
    class Hit(val t: Double = Double.MAX_VALUE) : Shadow {
        override fun isHit() = true
    }
}
