package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.brdf.BRDF.Sample
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.textures.Texture
import kotlin.math.pow

class SVGlossySpecular(
    var ks: Double = 0.0,
    var cs: Texture? = null,
    var exp: Double = 0.0,
    val sampler : Sampler = Sampler()
) : BRDF {

    override fun f(sr: IShade, wo: Vector3D, wi: Vector3D): Color {
        cs!!
        val nDotWi = wi dot sr.normal
        val r = (wi * -1.0) + sr.normal.toVector3D() * (2 * nDotWi)
        val rDotWo = r dot wo
        return if (rDotWo > 0) {
            cs!!.getColor(sr) * (ks * rDotWo.pow(exp))
        } else {
            Color.BLACK
        }
    }

    override fun sampleF(sr: IShade, wo: Vector3D): Sample {
        cs!!
        val nDotWo = wo dot sr.normal

        val w = wo * -1.0 + sr.normal.toVector3D() * (2.0 * nDotWo)
        val u = (Vector3D(0.00424, 1.0, 0.00764) cross w).normalize()
        val v = u cross w

        val sp = sampler.sampleSphere()
        var wi = ((u * sp.x) + v * sp.y) + w * sp.z
        if (wi dot sr.normal < 0.0) {
            wi = u * -sp.x + v * -sp.y + w * -sp.z
        }

        val phongLobe = (wi dot w).pow(exp)

        return Sample(wi = wi, pdf = phongLobe * (wi dot sr.normal), color = cs!!.getColor(sr) * (ks * phongLobe))
    }

    override fun rho(sr: IShade, wo: Vector3D): Color {
        throw RuntimeException("GlossySpecular.rho")
    }
}

