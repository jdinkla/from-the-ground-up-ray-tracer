package net.dinkla.raytracer.materials

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.brdf.FresnelReflector
import net.dinkla.raytracer.btdf.FresnelTransmitter
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.WrappedFloat
import net.dinkla.raytracer.worlds.World

class Dielectric : Phong() {

    internal var fresnelBrdf: FresnelReflector
    internal var fresnelBtdf: FresnelTransmitter

    internal var cfIn: Color
    internal var cfOut: Color

    init {
        fresnelBrdf = FresnelReflector()
        fresnelBtdf = FresnelTransmitter()
        cfIn = Color.WHITE
        cfOut = Color.WHITE
    }

    override fun shade(world: World, sr: Shade): Color {
        var L = super.shade(world, sr)
        val wo = sr.ray.direction.negate()
        val t = WrappedFloat.createMax()
        val sample = fresnelBrdf.sampleF(sr, wo)
        val reflectedRay = Ray(sr.hitPoint, sample.wi!!)
        val nDotWi = sr.normal.dot(sample.wi!!)

        if (fresnelBtdf.isTir(sr)) {
            val lr = world.tracer.trace(reflectedRay, t, sr.depth + 1)
            if (nDotWi < 0) {
                // reflected ray is inside
                L = L.plus(cfIn.pow(t.value!!).times(lr))
            } else {
                L = L.plus(cfOut.pow(t.value!!).times(lr))
            }
        } else {
            // no total internal reflection
            val sampleT = fresnelBtdf.sampleF(sr, wo)
            val transmittedRay = Ray(sr.hitPoint, sampleT.wt!!)
            val nDotWt = sr.normal.dot(sampleT.wt!!)
            if (nDotWi < 0) {
                // reflected ray is inside
                val c1 = world.tracer.trace(reflectedRay, t, sr.depth + 1)
                val c2 = c1.times(Math.abs(nDotWi))
                val lr = sample.color!!.times(c2)
                L = L.plus(cfIn.pow(t.value!!).times(lr))

                // transmitted ray is outside
                val c3 = world.tracer.trace(transmittedRay, t, sr.depth + 1)
                val c4 = c3.times(Math.abs(nDotWt))
                val lt = sampleT.color!!.times(c4)
                L = L.plus(cfOut.pow(t.value!!).times(lt))
            } else {
                // reflected ray is inside
                val c1 = world.tracer.trace(reflectedRay, t, sr.depth + 1)
                val c2 = c1.times(Math.abs(nDotWi))
                val lr = sample.color!!.times(c2)
                L = L.plus(cfOut.pow(t.value!!).times(lr))

                // transmitted ray is outside
                val c3 = world.tracer.trace(transmittedRay, t, sr.depth + 1)
                val c4 = c3.times(Math.abs(nDotWt))
                val lt = sampleT.color!!.times(c4)
                L = L.plus(cfIn.pow(t.value!!).times(lt))
            }

        }

        return L
    }

    fun setEtaIn(etaIn: Double) {

    }

    fun setEtaOut(etaOut: Double) {

    }


    fun setCfIn(cfIn: Color) {

    }

    fun setCfOut(cfOut: Color) {

    }

}
