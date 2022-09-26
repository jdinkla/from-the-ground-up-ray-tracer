package net.dinkla.raytracer.hits

open class ShadowHit(var t: Double = Double.MAX_VALUE)

sealed interface Shadow {
    object None : Shadow
    class Hit(val t: Double = Double.MAX_VALUE) : Shadow
}
