package net.dinkla.raytracer.materials

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.shouldBeApprox
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.world.IWorld

/**
 * Tests [GlossyReflector.pathShade] (TASK-46, Suffern ch. 26 exercise 26.9). In the path tracer a
 * glossy reflector contributes only its glossy reflection (no direct ambient/diffuse/specular term):
 * it importance-samples a direction within the Phong lobe around the mirror direction
 * ([net.dinkla.raytracer.brdf.GlossySpecular.sampleF]), traces that ray one level deeper, and weights
 * the incoming radiance by `color * (n . wi) / pdf`.
 *
 * The incident ray comes straight down the normal, so the mirror direction is the normal and every
 * sampled direction lies in the upper hemisphere (`n . wi > 0`, `pdf > 0`). Under importance sampling
 * the Phong lobe and the pdf cancel:
 *
 *   color * (n . wi) / pdf = cs*ks*lobe * (n . wi) / (lobe * (n . wi)) = cs * ks
 *
 * so the result is the deterministic `cr * kr * incoming` regardless of which direction is sampled —
 * which is what lets this test assert an exact value despite the randomness.
 */
internal class GlossyReflectorPathShadeTest :
    StringSpec({

        fun stubTracer(
            traced: Color,
            recordDepth: (Int) -> Unit = {},
        ): Tracer =
            object : Tracer {
                override fun trace(
                    ray: Ray,
                    depth: Int,
                ): Color {
                    recordDepth(depth)
                    return traced
                }
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
        fun shade(): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D.ORIGIN, Vector3D(0.0, -1.0, 0.0))
                override val hitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = 0
                override val material: IMaterial? = null
                override var normal: Normal = Normal.UP
                override var t: Double = 0.0
                override var geometricObject: IGeometricObject? = null
            }

        "path shade weights the reflected radiance by the glossy reflectance cr*kr" {
            val incoming = Color(0.5, 0.5, 0.5)
            val cr = Color(0.2, 0.4, 0.9)
            val kr = 0.6
            val material = GlossyReflector().apply { exp = 100.0; this.kr = kr; this.cr = cr }

            val result = material.pathShade(world(stubTracer(incoming)), shade())

            // The lobe and pdf cancel under importance sampling, leaving exactly cr * kr * incoming.
            result shouldBeApprox (cr * kr * incoming)
        }

        "path shade traces the reflected ray one recursion level deeper" {
            val material = GlossyReflector().apply { exp = 100.0; kr = 0.6; cr = Color.WHITE }
            var seenDepth = -1
            val sr = shade().apply { depth = 2 }

            material.pathShade(world(stubTracer(Color.WHITE) { seenDepth = it }), sr)

            seenDepth shouldBe 3
        }

        "path shade returns black when the world has no tracer to recurse through" {
            val material = GlossyReflector().apply { exp = 100.0; kr = 0.6; cr = Color(0.2, 0.4, 0.9) }

            val result = material.pathShade(world(tracer = null), shade())

            result shouldBeApprox Color.BLACK
        }
    })
