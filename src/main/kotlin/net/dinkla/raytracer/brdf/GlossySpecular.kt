package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.math.Vector3D
import java.util.*

class GlossySpecular(// specular intensity
        var ks: Double = 0.25,// specular color
        var cs: Color = Color.WHITE,// specular exponent
        var exp: Double = 5.0) : BRDF() {

    override fun f(sr: Shade, wo: Vector3D, wi: Vector3D): Color {
        val nDotWi = wi dot sr.normal
        val r = (wi * (-1.0)) + (Vector3D(sr.normal) * (2 * nDotWi))
        val rDotWo = r dot wo
        return if (rDotWo > 0) {
            cs * (ks * Math.pow(rDotWo, exp))
        } else {
            Color.BLACK
        }
    }

    override fun sampleF(sr: Shade, wo: Vector3D): BRDF.Sample {
        val sample = Sample()
        val nDotWo = sr.normal dot wo
        val r = -wo + (sr.normal * (2 * nDotWo))

        val u = Vector3D(0.00424, 1.0, 0.00764).cross(r).normalize()
        val v = u.cross(r)

        val sp = sampler!!.sampleHemisphere()
        sample.wi = u.times(sp.x).plus(v.times(sp.y)).plus(r.times(sp.z))
        val nDotWi = sr.normal.dot(sample.wi!!)
        if (nDotWi < 0) {
            sample.wi = u.times(-sp.x).plus(v.times(-sp.y)).plus(r.times(-sp.z))
        }

        val phongLobe = Math.pow(sample.wi!!.dot(r), exp)

        sample.pdf = phongLobe * nDotWi
        sample.color = cs * (ks * phongLobe)
        return sample
    }

    override fun rho(sr: Shade, wo: Vector3D): Color {
        throw RuntimeException("GlossySpecular.rho")
    }

    override fun toString(): String {
        return "GlossySpecular($ks, $cs, $exp)"
    }

    override fun equals(other: Any?): Boolean {
        if (other != null && other is GlossySpecular) {
            return ks == other.ks && exp == other.exp && cs == other.cs
        }
        return false
    }

    override fun hashCode(): Int = Objects.hash(ks, exp, cs)
}