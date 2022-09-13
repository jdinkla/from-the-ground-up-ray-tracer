package net.dinkla.raytracer.materials

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.brdf.GlossySpecular
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.world.World

class GlossyReflector : Phong() {

    private val glossySpecularBrdf: GlossySpecular = GlossySpecular()

    // TODO kr == ks? it was like that in the orig code, check the book!
    var kr: Double
        get() = glossySpecularBrdf.ks
        set(v: Double) {
            glossySpecularBrdf.ks = v
        }

    override var exp : Double
        get() = glossySpecularBrdf.exp
        set(v: Double) {
            glossySpecularBrdf.exp = v
        }

    override fun areaLightShade(world: World, sr: Shade): Color {
        val L = super.areaLightShade(world, sr)
        val wo = -sr.ray.direction
        val result = glossySpecularBrdf.sampleF(sr, wo)
        val reflectedRay = Ray(sr.hitPoint, result.wi)
        val r = world.tracer?.trace(reflectedRay, sr.depth + 1) ?: Color.WHITE
        val r2 = result.color * r
        val r3 = r2 * ((result.wi dot sr.normal) / result.pdf)
        val c = result.color
        return c
        //        L = L.plus(r);
        //        return L;
    }

}
