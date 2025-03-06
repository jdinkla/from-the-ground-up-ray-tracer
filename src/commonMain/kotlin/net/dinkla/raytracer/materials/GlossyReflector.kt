package net.dinkla.raytracer.materials

import net.dinkla.raytracer.brdf.GlossySpecular
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.world.IWorld

class GlossyReflector : Phong() {
    private val glossySpecularBrdf = GlossySpecular()

    var kr: Double
        get() = glossySpecularBrdf.ks
        set(v) {
            glossySpecularBrdf.ks = v
        }

    override var exp: Double
        get() = glossySpecularBrdf.exp
        set(v) {
            glossySpecularBrdf.exp = v
        }

    override fun areaLightShade(
        world: IWorld,
        sr: IShade,
    ): Color {
        val wo = -sr.ray.direction
        val result = glossySpecularBrdf.sampleF(sr, wo)
        val c = result.color
        return c
    }
}
