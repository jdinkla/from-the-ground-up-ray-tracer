package net.dinkla.raytracer.lights

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.IShade
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
 * Tests [AmbientOccluder]: when no sampled ray is occluded it returns the full ambient term, and when
 * every sampled ray is occluded it returns black. The `getDirection` path is exercised separately. A
 * deterministic [Regular] sampler with hemisphere samples is used so the result is reproducible.
 */
internal class AmbientOccluderTest :
    StringSpec({

        fun hemisphereSampler(
            numSamples: Int,
            numSets: Int = 1,
        ): Sampler =
            Sampler(Regular, numSamples, numSets).apply { mapSamplesToHemiSphere(1.0) }

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

        "returns the full ambient colour when no sampled ray is occluded" {
            // numSamples = 4 (a perfect square so Regular generates exactly 4 points).
            val occluder = AmbientOccluder(hemisphereSampler(numSamples = 4), numSamples = 4)
            occluder.ls = 1.0
            occluder.color = Color.WHITE

            val result = occluder.l(world(shadowed = false), shade())

            // ratio = 1 - 0/4 = 1 => color * (ls * 1) = WHITE.
            result shouldBeApprox Color.WHITE
        }

        "returns black when every sampled ray is occluded" {
            val occluder = AmbientOccluder(hemisphereSampler(numSamples = 4), numSamples = 4)
            occluder.ls = 1.0
            occluder.color = Color.WHITE

            val result = occluder.l(world(shadowed = true), shade())

            // ratio = 1 - 4/4 = 0 => black.
            result shouldBeApprox Color.BLACK
        }

        "getDirection returns a hemisphere direction with a positive component along the surface normal" {
            val occluder = AmbientOccluder(hemisphereSampler(numSamples = 4), numSamples = 4)

            val dir = occluder.getDirection(shade())

            // Hemisphere samples have a positive z in local space; the surface normal is up, so the
            // sampled world direction must point into the upper hemisphere (positive y component).
            dir.y shouldBeGreaterThan 0.0
        }
    })
