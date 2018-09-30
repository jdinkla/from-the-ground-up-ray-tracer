package net.dinkla.raytracer.materials

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.worlds.World

class Emissive : IMaterial {

    var ls: Double = 0.toDouble()
    var ce: Color

    protected var cachedLe: Color? = null

    constructor() {
        ls = 1.0
        ce = Color.WHITE
    }

    constructor(ce: Color, ls: Double) {
        this.ce = ce
        this.ls = ls
    }

    override fun shade(world: World, sr: Shade): Color {
        throw RuntimeException("Emissive.shade")
    }

    override fun areaLightShade(world: World, sr: Shade): Color {
        return if ( (-(sr.normal)) dot (sr.ray.direction) > 0) {
            getLe(sr)
        } else {
            Color.BLACK
        }
    }

    override fun getLe(sr: Shade): Color {
        var c = cachedLe
        if (null == c) {
            c = ce.times(ls)
            cachedLe = c
        }
        return c
    }

}
