package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.brdf.BRDF.Sample
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.MathUtils.INV_PI
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.samplers.Sampler

/**
 * The perfectly diffuse (Lambertian) BRDF from Suffern ch. 13/26. [f] is the constant reflectance
 * `cd * kd / PI`; [rho] the bi-hemispherical reflectance `cd * kd`.
 *
 * [sampleF] does **cosine-weighted hemisphere sampling** around the surface normal — the importance
 * sampling the path tracer (Suffern ch. 26) needs to estimate indirect diffuse light. A shared
 * cosine-weighted [Sampler] (`mapSamplesToHemiSphere(1.0)`) supplies the points; the sampled
 * direction `wi` then has pdf `cos(theta) / PI`.
 *
 * The sampler is intentionally **not** part of the data-class identity: equality and hash compare
 * only the reflectance ([kd], [cd]), so two Lambertians with the same coefficients stay equal (the
 * direct-lighting materials such as [net.dinkla.raytracer.materials.Matte] rely on this).
 */
data class Lambertian(
    var kd: Double = 1.0,
    var cd: Color = Color.WHITE,
) : BRDF {
    /**
     * Cosine-weighted hemisphere sampler (`exp = 1` gives a `cos(theta)` density), used only by
     * [sampleF]. Declared outside the primary constructor so it stays out of the data-class identity.
     */
    private val sampler: Sampler = Sampler().apply { mapSamplesToHemiSphere(1.0) }

    init {
        if (kd < 0.0 || kd > 1.0) {
            throw AssertionError("kd: diffuse reflection coefficient, in [0,1]")
        }
    }

    override fun f(
        sr: IShade,
        wo: Vector3D,
        wi: Vector3D,
    ): Color = f

    /**
     * Samples an outgoing direction `wi` cosine-weighted about the surface normal and returns it
     * together with the BRDF value [f] and the matching pdf `(n . wi) / PI`. The direction is built
     * by mapping a cosine-weighted hemisphere sample into the orthonormal basis `(u, v, w=normal)`,
     * so `n . wi == sample.z == cos(theta)` by construction.
     */
    override fun sampleF(
        sr: IShade,
        wo: Vector3D,
    ): Sample {
        val w = sr.normal.toVector3D()
        val u = (UP_JITTER cross w).normalize()
        val v = u cross w
        val sp = sampler.sampleHemisphere()
        val wi = ((u * sp.x) + (v * sp.y) + (w * sp.z)).normalize()
        val pdf = (sr.normal dot wi) * INV_PI
        return Sample(wi = wi, color = f, pdf = pdf)
    }

    override fun rho(
        sr: IShade,
        wo: Vector3D,
    ): Color = rho

    val f: Color
        get() = cd * (kd * INV_PI)

    private val rho: Color
        get() = cd * kd

    private companion object {
        /**
         * A slightly tilted up vector used to form the tangent basis; not axis-aligned so the cross
         * product with a vertical normal never degenerates to the zero vector.
         */
        val UP_JITTER = Vector3D(0.0034, 1.0, 0.0071)
    }
}
