package net.dinkla.raytracer.lights

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.materials.Emissive
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.materials.Matte
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.shouldBeApprox
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.world.IWorld

/**
 * Tests [AreaLight] in isolation with hand-written fakes. The geometry is built so the numbers are
 * hand-computable: the hit point is the origin with the surface normal up, the source samples the
 * point one unit above with its normal facing down, so `wi = (0,1,0)` and `nDotD = 1`. This exercises
 * the `l`, `G`, `inShadow`, `nDotD` and `getSamples` paths plus the throwing direct-lighting overrides
 * that an AREA light must not be called through.
 */
internal class AreaLightTest :
    StringSpec({

        val samplePoint = Point3D(0.0, 1.0, 0.0)
        val sourceNormal = Normal.DOWN
        val emissive = Emissive(Color.WHITE, 1.0)

        fun source(): ILightSource =
            object : ILightSource {
                override fun sample(): Point3D = samplePoint

                override fun pdf(sr: IShade): Double = 0.5

                override fun getNormal(p: Point3D): Normal = sourceNormal

                override fun getLightMaterial(): IMaterial = emissive
            }

        fun shade(
            theMaterial: IMaterial?,
            theNormal: Normal = Normal.UP,
        ): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D.ORIGIN, Vector3D(0.0, 0.0, -1.0))
                override val hitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = 0
                override val material: IMaterial? = theMaterial
                override var normal: Normal = theNormal
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

        fun areaLight(): AreaLight =
            AreaLight(shadows = true).apply {
                this.source = source()
                this.material = emissive
                this.numSamples = 3
            }

        "getSamples returns one sample per numSamples with a direction toward the light" {
            val light = areaLight()
            val sr = shade(emissive)

            val samples = light.getSamples(sr)

            samples.size shouldBe 3
            // wi points from the origin toward (0,1,0): straight up.
            samples.first().wi!! shouldBeApprox Vector3D(0.0, 1.0, 0.0)
        }

        "nDotD is positive when the source normal faces the surface" {
            val light = areaLight()
            val sr = shade(emissive)
            val sample = light.getSamples(sr).first()

            // -lightNormal = (0,1,0), wi = (0,1,0) => dot = 1.
            sample.nDotD shouldBeApprox 1.0
        }

        // TASK-54: l must read the LIGHT EMITTER's own radiance (getLightMaterial().getLe), not the
        // receiving surface's getLe. The receiver here is a Matte with a deliberately different getLe
        // (cd*kd = (0.5,0.5,0.5)*0.5 = (0.25,0.25,0.25)), so the WHITE result can only come from the
        // light's Emissive(WHITE, 1.0) emitter — pinning the corrected emitter-radiance source.
        "l returns the light emitter's radiance when the sample faces the surface (nDotD > 0)" {
            val light = areaLight()
            val receiver = Matte(Color(0.5, 0.5, 0.5), ka = 0.25, kd = 0.5)
            val sr = shade(receiver)
            val sample = light.getSamples(sr).first()

            val radiance = light.l(sr, sample)

            // Emissive(WHITE, 1.0).getLe = WHITE; the receiver Matte's getLe (0.25,...) is NOT read.
            radiance shouldBeApprox Color.WHITE
        }

        "l returns black when the sample faces away from the surface (nDotD <= 0)" {
            val light = areaLight()
            // Surface normal down while light is above => same source normal, but wi flips sign of nDotD.
            val sr = shade(emissive)
            // Manually build a sample whose direction makes nDotD negative.
            val sample = light.Sample()
            sample.samplePoint = Point3D(0.0, 1.0, 0.0)
            sample.lightNormal = Normal.UP // -normal = (0,-1,0)
            sample.wi = Vector3D(0.0, 1.0, 0.0) // dot = -1 <= 0

            val radiance = light.l(sr, sample)

            radiance shouldBeApprox Color.BLACK
        }

        "G is nDotD over the squared distance to the sample point" {
            val light = areaLight()
            val sr = shade(emissive)
            val sample = light.getSamples(sr).first()

            // nDotD = 1, squared distance from origin to (0,1,0) = 1 => G = 1.
            light.G(sr, sample) shouldBeApprox 1.0
        }

        "inShadow delegates the projected light distance to the world" {
            val light = areaLight()
            val sr = shade(emissive)
            val sample = light.getSamples(sr).first()
            val shadowRay = Ray(Point3D.ORIGIN, Vector3D(0.0, 1.0, 0.0))

            light.inShadow(world(shadowed = true), shadowRay, sr, sample) shouldBe true
            light.inShadow(world(shadowed = false), shadowRay, sr, sample) shouldBe false
        }

        "pdf delegates to the source" {
            val light = areaLight()
            val sr = shade(emissive)

            light.pdf(sr) shouldBeApprox 0.5
        }

        "getLightMaterial returns the assigned material" {
            val light = areaLight()

            light.getLightMaterial() shouldBe emissive
        }

    })
