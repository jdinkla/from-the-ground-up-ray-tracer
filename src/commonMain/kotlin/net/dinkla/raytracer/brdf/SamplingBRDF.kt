package net.dinkla.raytracer.brdf

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.math.Vector3D

/**
 * The importance-sampling role of a BRDF: given the outgoing direction [wo], draw an incoming
 * direction together with the BRDF value and its pdf ([Sample]). This is the role the recursive and
 * Monte-Carlo materials need — mirrors (`PerfectSpecular`/`FresnelReflector`), glossy reflection
 * (`GlossySpecular`), and cosine-weighted diffuse sampling (`Lambertian`).
 *
 * Split from the evaluation role ([BRDF.f]) so that diffuse-only BRDFs that cannot be sampled (e.g.
 * the spatially-varying `SvLambertian`) need not provide an unsupported [sampleF]. See TASK-63.
 */
interface SamplingBRDF {
    class Sample(
        val wi: Vector3D,
        val color: Color,
        val pdf: Double,
    )

    fun sampleF(
        sr: IShade,
        wo: Vector3D,
    ): Sample
}
