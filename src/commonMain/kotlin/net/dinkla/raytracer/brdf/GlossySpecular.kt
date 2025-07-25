package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.brdf.BRDF.Sample
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.samplers.Sampler
import java.util.Objects
import kotlin.math.pow

data class GlossySpecular(
    var ks: Double = 0.25,
    var cs: Color = Color.WHITE,
    var exp: Double = 5.0,
    val sampler: Sampler = Sampler(),
) : BRDF {
    override fun f(
        sr: IShade,
        wo: Vector3D,
        wi: Vector3D,
    ): Color {
        val nDotWi = wi dot sr.normal
        val r = (wi * (-1.0)) + (sr.normal.toVector3D() * (2 * nDotWi))
        val rDotWo = r dot wo
        return if (rDotWo > 0) {
            cs * (ks * rDotWo.pow(exp))
        } else {
            Color.BLACK
        }
    }

    override fun sampleF(
        sr: IShade,
        wo: Vector3D,
    ): Sample {
        val nDotWo = sr.normal dot wo
        val r = -wo + (sr.normal * (2 * nDotWo))
        val u = (Vector3D(0.00424, 1.0, 0.00764) cross r).normalize()
        val v = u cross r
        val sp = sampler.sampleHemisphere()
        var wi = ((u * sp.x) + v * sp.y) + r * sp.z
        val nDotWi = sr.normal dot wi
        if (nDotWi < 0) {
            wi = ((u * -sp.x) + v * -sp.y) + r * -sp.z
        }
        val phongLobe = (wi dot r).pow(exp)
        return Sample(wi = wi, pdf = phongLobe * nDotWi, color = cs * (ks * phongLobe))
    }

    override fun rho(
        sr: IShade,
        wo: Vector3D,
    ): Color = throw RuntimeException("GlossySpecular.rho")

    override fun equals(other: Any?): Boolean {
        if (other != null && other is GlossySpecular) {
            return ks == other.ks && exp == other.exp && cs == other.cs
        }
        return false
    }

    override fun hashCode(): Int = Objects.hash(ks, exp, cs)
}
