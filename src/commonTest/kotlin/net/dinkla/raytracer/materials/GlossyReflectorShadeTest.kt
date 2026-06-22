package net.dinkla.raytracer.materials

import io.kotest.core.spec.style.StringSpec
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.math.WrappedDouble
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.shouldBeApprox
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.world.IWorld

/**
 * Pins [GlossyReflector.shade]: the glossy-reflection recursion of Suffern ch. 25. The world has no
 * lights and zero ambient, so the Phong direct term is black and `shade` returns exactly the glossy
 * reflection contribution. The incident ray comes straight down the normal so the mirror direction is
 * the normal itself; every importance-sampled direction then lies in the upper hemisphere, keeping
 * `n . wi > 0` and `pdf > 0` for any random sample. Under importance sampling the Phong lobe and the
 * pdf cancel, so the contribution is the deterministic `cr * kr * traced` regardless of which
 * direction is sampled — which is what lets this test assert an exact value despite the randomness.
 *
 * Calling `shade` also exercises [net.dinkla.raytracer.brdf.GlossySpecular.sampleF]; before this
 * feature that threw because the hemisphere sampler was never initialised, so this test also guards
 * that regression.
 */
internal class GlossyReflectorShadeTest :
    StringSpec({

        // Stub tracer: returns [traced] for every reflected ray, so the reflected radiance is exact.
        class StubTracer(
            private val traced: Color,
        ) : Tracer {
            override fun trace(
                ray: Ray,
                depth: Int,
            ): Color = traced

            override fun trace(
                ray: Ray,
                tmin: WrappedDouble,
                depth: Int,
            ): Color = traced
        }

        fun world(tracer: Tracer?): IWorld =
            object : IWorld {
                override var tracer: Tracer? = tracer
                override val lights: List<Light> = emptyList()
                override val ambientLight: Ambient = Ambient(ls = 0.0, color = Color.BLACK)
                override var backgroundColor: Color = Color.BLACK

                override fun hit(
                    ray: Ray,
                    sr: IHit,
                ): Boolean = false

                override fun inShadow(
                    ray: Ray,
                    sr: IShade,
                    d: Double,
                ): Boolean = false

                override fun shouldStopRecursion(depth: Int): Boolean = false
            }

        // Incident ray pointing straight down onto an up-facing surface: wo = -ray.direction = the normal.
        val sr: IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D.ORIGIN, Vector3D(0.0, -1.0, 0.0))
                override val hitPoint: Point3D = Point3D.ORIGIN
                override val localHitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = 0
                override val material: IMaterial? = null
                override var normal: Normal = Normal.UP
                override var t: Double = 0.0
                override var geometricObject: IGeometricObject? = null
            }

        "glossy reflection adds cr*kr times the traced colour to the (here black) direct shading" {
            val traced = Color(0.5, 0.5, 0.5)
            val cr = Color(0.2, 0.4, 0.9)
            val kr = 0.6
            val material =
                GlossyReflector().apply {
                    exp = 100.0
                    this.kr = kr
                    this.cr = cr
                }

            val color = material.shade(world(StubTracer(traced)), sr)

            // Phong direct term is black (no lights, no ambient); the lobe and pdf cancel under
            // importance sampling, leaving exactly cr * kr * traced.
            val expected =
                Color(
                    cr.red * kr * traced.red,
                    cr.green * kr * traced.green,
                    cr.blue * kr * traced.blue,
                )
            color shouldBeApprox expected
        }

        "without a tracer there is no glossy reflection contribution" {
            val material =
                GlossyReflector().apply {
                    exp = 100.0
                    kr = 0.6
                    cr = Color(0.2, 0.4, 0.9)
                }

            val color = material.shade(world(tracer = null), sr)

            color shouldBeApprox Color.BLACK
        }
    })
