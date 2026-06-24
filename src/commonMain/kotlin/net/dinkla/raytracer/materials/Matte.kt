package net.dinkla.raytracer.materials

import net.dinkla.raytracer.brdf.Lambertian
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.colors.ColorAccumulator
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.lights.AreaLight
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.utilities.equals
import net.dinkla.raytracer.world.IWorld
import java.util.Objects

@Suppress("TooManyFunctions")
open class Matte(
    color: Color = Color.WHITE,
    ka: Double = 0.25,
    kd: Double = 0.75,
) : IMaterial {
    protected val ambientBRDF = Lambertian(ka, color)
    protected val diffuseBRDF = Lambertian(kd, color)

    var ka: Double
        get() = ambientBRDF.kd
        set(v) {
            ambientBRDF.kd = v
        }

    var kd: Double
        get() = diffuseBRDF.kd
        set(v) {
            diffuseBRDF.kd = v
        }

    var cd: Color
        get() = ambientBRDF.cd
        set(v) {
            ambientBRDF.cd = v
            diffuseBRDF.cd = v
        }

    override fun shade(
        world: IWorld,
        sr: IShade,
    ): Color {
        val wo = -sr.ray.direction
        var L = getAmbientColor(world, sr, wo)
        for (light in world.lights) {
            val wi = light.getDirection(sr)
            val nDotWi = wi dot sr.normal
            if (nDotWi > 0) {
                var inShadow = false
                if (light.shadows) {
                    val shadowRay = Ray(sr.hitPoint, wi)
                    inShadow = light.inShadow(world, shadowRay, sr)
                }
                if (!inShadow) {
                    val f = diffuseBRDF.f(sr, wo, wi)
                    val l = light.l(world, sr)
                    L += (f * l) * nDotWi
                }
            }
        }
        return L
    }

    override fun areaLightShade(
        world: IWorld,
        sr: IShade,
    ): Color {
        val wo = -sr.ray.direction
        val L = getAmbientColor(world, sr, wo)
        val S = ColorAccumulator()
        for (light in world.lights.filterIsInstance<AreaLight>()) {
            for (sample in light.getSamples(sr)) {
                sampleContribution(world, sr, wo, light, sample)?.let { S + it }
            }
        }
        return L + S.average
    }

    /**
     * The diffuse contribution of a single area-light sample, or `null` when the sample does not
     * contribute (it faces away from the surface or is occluded).
     */
    private fun sampleContribution(
        world: IWorld,
        sr: IShade,
        wo: Vector3D,
        light: AreaLight,
        sample: AreaLight.Sample,
    ): Color? {
        val wi = requireNotNull(sample.wi) { "Sample.wi not set; call getSamples first" }
        val nDotWi = wi dot sr.normal
        if (nDotWi <= 0 || isInShadow(world, sr, wi, light, sample)) return null
        val f = diffuseBRDF.f(sr, wo, wi)
        val l = light.l(world, sr, sample)
        val f1 = light.G(sr, sample) / light.pdf(sr)
        return (f * l) * nDotWi * f1
    }

    private fun isInShadow(
        world: IWorld,
        sr: IShade,
        wi: Vector3D,
        light: AreaLight,
        sample: AreaLight.Sample,
    ): Boolean {
        if (!light.shadows) return false
        val shadowRay = Ray(sr.hitPoint, wi)
        return light.inShadow(world, shadowRay, sr, sample)
    }

    /**
     * The diffuse path-tracing shade (Suffern ch. 26): importance-sample an indirect direction with
     * the cosine-weighted diffuse BRDF, trace the reflected ray one level deeper through the world's
     * tracer, and weight the incoming radiance by `f * (n . wi) / pdf`. With cosine-weighted sampling
     * the geometry term `(n . wi)` and the pdf `(n . wi) / PI` largely cancel, leaving the indirect
     * diffuse estimate that produces colour bleeding and soft indirect light.
     */
    override fun pathShade(
        world: IWorld,
        sr: IShade,
    ): Color {
        val wo = -sr.ray.direction
        val sample = diffuseBRDF.sampleF(sr, wo)
        val nDotWi = sr.normal dot sample.wi
        val tracer = world.tracer
        return if (nDotWi <= 0.0 || sample.pdf <= 0.0 || tracer == null) {
            Color.BLACK
        } else {
            val reflectedRay = Ray(sr.hitPoint, sample.wi)
            val incoming = tracer.trace(reflectedRay, sr.depth + 1)
            (sample.color * incoming) * (nDotWi / sample.pdf)
        }
    }

    /**
     * The hybrid global-illumination shade (Suffern ch. 26, Listing 26.7) used by
     * [net.dinkla.raytracer.tracers.GlobalTrace]. At the **first** hit (`sr.depth == 0`) it returns
     * the **direct** illumination by sampling the lights ([areaLightShade], as in ch. 18) *plus* one
     * indirect diffuse bounce ([pathShade]). At **deeper** bounces (`sr.depth > 0`) it returns only the
     * indirect bounce — the direct light was already sampled at the first hit, so adding it again would
     * double-count it (Fig 26.11). The indirect term is identical to [pathShade]; emitters reached by
     * that bounce are kept consistent by [Emissive.globalShade], which suppresses its emission at
     * `sr.depth == 1`.
     */
    override fun globalShade(
        world: IWorld,
        sr: IShade,
    ): Color {
        val direct = if (sr.depth == 0) globalDirect(world, sr) else Color.BLACK
        return direct + pathShade(world, sr)
    }

    /**
     * The **direct** illumination term of [globalShade] (Suffern ch. 26, Listing 26.7): the diffuse
     * surface samples every [AreaLight] in the scene and sums their contributions, exactly like
     * [areaLightShade]'s diffuse loop, but with one deliberate difference — the incoming radiance is the
     * **light's** emitted radiance (`light.getLightMaterial().getLe`), not the receiving surface's
     * `getLe` that [areaLightShade] reads through [AreaLight.l]. That distinction matters for the global
     * tracer: the direct term must carry the panel's full intensity (e.g. `ce * ls`) so it is bright
     * enough to dominate, smooth and low-noise, the indirect path estimate (book Fig 26.12). The ambient
     * term is omitted because the global tracer integrates indirect light directly.
     */
    private fun globalDirect(
        world: IWorld,
        sr: IShade,
    ): Color {
        val wo = -sr.ray.direction
        val s = ColorAccumulator()
        for (light in world.lights.filterIsInstance<AreaLight>()) {
            val le = light.getLightMaterial().getLe(sr)
            for (sample in light.getSamples(sr)) {
                globalSampleContribution(world, sr, wo, light, sample, le)?.let { s + it }
            }
        }
        return s.average
    }

    /**
     * One area-light sample's diffuse contribution for [globalDirect]: `null` when the sample faces
     * away or is occluded. Mirrors [sampleContribution] but multiplies by the supplied light radiance
     * [le] (the emitter's `getLe`) instead of [AreaLight.l] (the receiver's `getLe`).
     */
    private fun globalSampleContribution(
        world: IWorld,
        sr: IShade,
        wo: Vector3D,
        light: AreaLight,
        sample: AreaLight.Sample,
        le: Color,
    ): Color? {
        val wi = requireNotNull(sample.wi) { "Sample.wi not set; call getSamples first" }
        val nDotWi = wi dot sr.normal
        if (nDotWi <= 0 || sample.nDotD <= 0 || isInShadow(world, sr, wi, light, sample)) return null
        val f = diffuseBRDF.f(sr, wo, wi)
        val f1 = light.G(sr, sample) / light.pdf(sr)
        return (f * le) * nDotWi * f1
    }

    protected fun getAmbientColor(
        world: IWorld,
        sr: IShade,
        wo: Vector3D,
    ): Color {
        val c1 = ambientBRDF.rho(sr, wo)
        val c2 = world.ambientLight.l(world, sr)
        return c1 * c2
    }

    override fun getLe(sr: IShade): Color = diffuseBRDF.rho(sr, Vector3D.UP)

    override fun equals(other: Any?): Boolean =
        this.equals<Matte>(other) { a, b ->
            a.ambientBRDF == b.ambientBRDF && a.diffuseBRDF == b.diffuseBRDF
        }

    override fun hashCode(): Int = Objects.hash(ambientBRDF, diffuseBRDF)

    override fun toString(): String = "Matte($ambientBRDF,$diffuseBRDF)"
}
