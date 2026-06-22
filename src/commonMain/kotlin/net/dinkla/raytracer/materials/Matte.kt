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
