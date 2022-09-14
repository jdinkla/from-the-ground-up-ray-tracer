package net.dinkla.raytracer.materials

import net.dinkla.raytracer.brdf.FresnelReflector
import net.dinkla.raytracer.btdf.FresnelTransmitter
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.WrappedDouble
import net.dinkla.raytracer.world.IWorld
import kotlin.math.abs

class Dielectric : Phong() {

    private var fresnelBrdf: FresnelReflector = FresnelReflector()
    private var fresnelBtdf: FresnelTransmitter = FresnelTransmitter()

    private var cfIn: Color = Color.WHITE
    private var cfOut: Color = Color.WHITE

    override fun shade(world: IWorld, sr: Shade): Color {
        var L = super.shade(world, sr)
        val wo = -sr.ray.direction
        val t = WrappedDouble.createMax()
        val sample = fresnelBrdf.sampleF(sr, wo)
        val reflectedRay = Ray(sr.hitPoint, sample.wi)
        val nDotWi = sr.normal dot sample.wi

        if (fresnelBtdf.isTir(sr)) {
            val lr = world.tracer?.trace(reflectedRay, t, sr.depth + 1) ?: Color.WHITE
            if (nDotWi < 0) {
                // reflected ray is inside
                L += cfIn.pow(t.value) * lr
            } else {
                L += cfOut.pow(t.value) * lr
            }
        } else {
            // no total internal reflection
            val sampleT = fresnelBtdf.sampleF(sr, wo)
            val transmittedRay = Ray(sr.hitPoint, sampleT.wt)
            val nDotWt = sr.normal.dot(sampleT.wt)
            if (nDotWi < 0) {
                // reflected ray is inside
                val c1 = world.tracer?.trace(reflectedRay, t, sr.depth + 1) ?: Color.WHITE
                val c2 = c1 * abs(nDotWi)
                val lr = sample.color * c2
                L += cfIn.pow(t.value) * lr

                // transmitted ray is outside
                val c3 = world.tracer?.trace(transmittedRay, t, sr.depth + 1) ?: Color.WHITE
                val c4 = c3 * abs(nDotWt)
                val lt = sampleT.color * c4
                L += cfOut.pow(t.value) * lt
            } else {
                // reflected ray is inside
                val c1 = world.tracer?.trace(reflectedRay, t, sr.depth + 1) ?: Color.WHITE
                val c2 = c1 * abs(nDotWi)
                val lr = sample.color * c2
                L += cfOut.pow(t.value) * lr

                // transmitted ray is outside
                val c3 = world.tracer?.trace(transmittedRay, t, sr.depth + 1) ?: Color.WHITE
                val c4 = c3.times(s = abs(nDotWt))
                val lt = sampleT.color * c4
                L += cfIn.pow(t.value) * lt
            }
        }
        return L
    }
}
