package net.dinkla.raytracer.materials

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.brdf.GlossySpecular
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.worlds.World

/**
 * Created by IntelliJ IDEA.
 * User: Jörn Dinkla
 * Date: 25.04.2010
 * Time: 17:14:03
 * To change this template use File | Settings | File Templates.
 */
class GlossyReflector : Phong() {

    protected val glossySpecularBrdf: GlossySpecular

    init {
        glossySpecularBrdf = GlossySpecular()
    }

    fun setKr(kr: Double) {
        glossySpecularBrdf.ks = kr
    }

    override fun setExp(exp: Double) {
        glossySpecularBrdf.exp = exp
    }

    override fun areaLightShade(world: World, sr: Shade): Color {
        val L = super.areaLightShade(world, sr)
        val wo = sr.ray.direction.negate()
        val result = glossySpecularBrdf.sampleF(sr, wo)
        val reflectedRay = Ray(sr.hitPoint, result.wi!!)
        val r = world.tracer.trace(reflectedRay, sr.depth + 1)
        val r2 = result.color!!.times(r)
        val r3 = r2.times(result.wi!!.dot(sr.normal) / result.pdf)
        val c = result.color
        return c ?: Color.BLACK
        //        L = L.plus(r);
        //        return L;
    }

    fun setSampler(sampler: Sampler) {
        glossySpecularBrdf.sampler = sampler
    }
}
