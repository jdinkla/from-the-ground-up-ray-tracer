package net.dinkla.raytracer.world.dsl

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.lights.AreaLight
import net.dinkla.raytracer.lights.DirectionalLight
import net.dinkla.raytracer.lights.EnvironmentLight
import net.dinkla.raytracer.lights.ILightSource
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.lights.PointLight
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.samplers.Sampler

/**
 * DSL receiver for the `lights { ... }` block. Each call appends one light to the scene; the
 * accumulated list is read back through [lights].
 */
class LightsScope {
    private val mutableLights: MutableList<Light> = mutableListOf()

    /** The lights declared so far, as an immutable snapshot. */
    val lights: List<Light>
        get() = mutableLights.toList()

    /** Adds a [PointLight] at [location] with intensity [ls] and the given [color]. */
    fun pointLight(
        location: Point3D = Point3D.ORIGIN,
        ls: Double = 0.0,
        color: Color = Color.WHITE,
    ) {
        mutableLights += PointLight(location, ls, color)
    }

    /** Adds a [DirectionalLight] travelling along [direction] with intensity [ls] and the given [color]. */
    fun directionalLight(
        direction: Vector3D = Vector3D.UP,
        ls: Double = 0.0,
        color: Color = Color.WHITE,
    ) {
        mutableLights +=
            DirectionalLight().apply {
                this.ls = ls
                this.setDirection(direction)
                this.color = color
            }
    }

    /**
     * Adds an [EnvironmentLight] whose incident radiance comes from [material] (typically an
     * [net.dinkla.raytracer.materials.SvEmissive] backed by an image texture, i.e. a spherical
     * environment map). Hemisphere directions are drawn from [sampler].
     */
    fun environmentLight(
        material: IMaterial,
        sampler: Sampler = Sampler(),
        shadows: Boolean = true,
    ) {
        mutableLights +=
            EnvironmentLight(shadows).apply {
                this.material = material
                this.sampler = sampler
            }
    }

    /**
     * Adds an [AreaLight] backed by the light source [of], sampled with [numSamples] shadow rays for
     * soft shadows; the light's material is taken from the source's emissive material.
     */
    fun areaLight(
        of: ILightSource,
        numSamples: Int,
    ) {
        val light =
            AreaLight().apply {
                this.source = of
                this.numSamples = numSamples
                this.material = of.getLightMaterial()
            }
        mutableLights += light
    }
}
