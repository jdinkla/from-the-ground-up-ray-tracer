package net.dinkla.raytracer.lights

import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.materials.Emissive
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.samplers.Regular
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.shouldBeApprox
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.world.IWorld

/**
 * Tests [EnvironmentLight]: `l` returns the assigned material's radiance, `getDirection` samples a
 * hemisphere direction around the surface normal, `inShadow` delegates to the world, and the missing
 * sampler/material guards throw a contextual [IllegalArgumentException].
 */
internal class EnvironmentLightTest :
    StringSpec({

        fun hemisphereSampler(): Sampler =
            Sampler(Regular, numSamples = 4, numSets = 1).apply { mapSamplesToHemiSphere(1.0) }

        fun shade(): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D.ORIGIN, Vector3D(0.0, 0.0, -1.0))
                override val hitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = 0
                override val material: IMaterial? = null
                override var normal: Normal = Normal.UP
                override var t: Double = 0.0
                override var geometricObject: IGeometricObject? = null
            }

        fun world(shadowed: Boolean): IWorld =
            object : IWorld {
                override var tracer: Tracer? = null
                override val lights: List<Light> = emptyList()
                override val ambientLight: Ambient = Ambient()
                override var backgroundColor: Color = Color.BLUE

                override fun hit(
                    ray: Ray,
                    sr: IHit,
                ): Boolean = false

                override fun inShadow(
                    ray: Ray,
                    sr: IShade,
                    d: Double,
                ): Boolean = shadowed

                override fun shouldStopRecursion(depth: Int): Boolean = true
            }

        "l returns the assigned emissive material's radiance" {
            val light =
                EnvironmentLight().apply {
                    material = Emissive(Color.GREEN, 1.0)
                    sampler = hemisphereSampler()
                }

            val result = light.l(world(shadowed = false), shade())

            result shouldBeApprox Color.GREEN
        }

        "getDirection samples a direction into the hemisphere around the surface normal" {
            val light =
                EnvironmentLight().apply {
                    material = Emissive(Color.GREEN, 1.0)
                    sampler = hemisphereSampler()
                }

            val dir = light.getDirection(shade())

            // Surface normal is up and u/v lie in the plane perpendicular to it, so the sampled
            // direction must carry the hemisphere's positive normal component into +y.
            (dir.y > 0.0) shouldBe true
            // getDirection also stores the chosen direction in wi.
            light.wi shouldBeApprox dir
        }

        "inShadow delegates to the world" {
            val light =
                EnvironmentLight().apply {
                    material = Emissive(Color.GREEN, 1.0)
                    sampler = hemisphereSampler()
                }
            val ray = Ray(Point3D.ORIGIN, Vector3D(0.0, 1.0, 0.0))

            light.inShadow(world(shadowed = true), ray, shade()) shouldBe true
            light.inShadow(world(shadowed = false), ray, shade()) shouldBe false
        }

        "l without a material throws a contextual error" {
            val light = EnvironmentLight().apply { sampler = hemisphereSampler() }

            val ex = shouldThrow<IllegalArgumentException> { light.l(world(shadowed = false), shade()) }
            ex.message!!.contains("material") shouldBe true
        }

        "getDirection without a sampler throws a contextual error" {
            val light = EnvironmentLight().apply { material = Emissive(Color.GREEN, 1.0) }

            val ex = shouldThrow<IllegalArgumentException> { light.getDirection(shade()) }
            ex.message!!.contains("sampler") shouldBe true
        }
    })
