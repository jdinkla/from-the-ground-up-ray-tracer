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
 * Tests [Reflective.pathShade] (TASK-46, Suffern ch. 26, Listing 26.5). A reflective surface in the
 * path tracer samples the single perfect-specular direction ([net.dinkla.raytracer.brdf.PerfectSpecular]),
 * traces that reflected ray one level deeper, and weights the incoming radiance by `f * (n . wi)`.
 *
 * For perfect specular reflection the sample is deterministic and the weight collapses analytically:
 * `PerfectSpecular.sampleF` returns `color = cr * (kr / |n . wi|)` and `pdf = 1`. Because the reflected
 * direction satisfies `n . wi > 0`, the per-sample weight is
 *
 *   sample.color * (n . wi) = cr * (kr / (n . wi)) * (n . wi) = cr * kr
 *
 * so the result equals `cr * kr * incoming` componentwise. Note that, unlike [Matte.pathShade], the
 * pdf is 1 and the geometry term is **not** divided out — matching Listing 26.5.
 */
internal class ReflectivePathShadeTest :
    StringSpec({

        // Incident ray hits the up-facing surface at 45 degrees so the reflected direction points up
        // and away (n . wi > 0).
        fun shade(): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D(-1.0, 1.0, 0.0), Vector3D(1.0, -1.0, 0.0))
                override val hitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = 0
                override val material: IMaterial? = null
                override var normal: Normal = Normal.UP
                override var t: Double = 0.0
                override var geometricObject: IGeometricObject? = null
            }

        fun world(
            theTracer: Tracer?,
        ): IWorld =
            object : IWorld {
                override var tracer: Tracer? = theTracer
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

                override fun shouldStopRecursion(depth: Int): Boolean = true
            }

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

        "path shade weights the reflected radiance by the mirror reflectance cr*kr" {
            val reflective = Reflective(Color.WHITE, 0.0, 0.0).apply { kr = 0.4; cr = Color(0.2, 0.5, 0.9) }
            val incoming = Color(0.3, 0.6, 0.8)

            val result = reflective.pathShade(world(stubTracer(incoming)), shade())

            // sample.color * (n.wi) == cr*kr, so the result is cr*kr*incoming componentwise.
            result shouldBeApprox (Color(0.2, 0.5, 0.9) * 0.4 * incoming)
        }

        "path shade reflects the incoming radiance unchanged for a perfect white mirror" {
            val reflective = Reflective(Color.WHITE, 0.0, 0.0).apply { kr = 1.0; cr = Color.WHITE }
            val incoming = Color(0.3, 0.5, 0.7)

            val result = reflective.pathShade(world(stubTracer(incoming)), shade())

            result shouldBeApprox incoming
        }

        "path shade traces the reflected ray one recursion level deeper" {
            val reflective = Reflective(Color.WHITE, 0.0, 0.0).apply { kr = 1.0; cr = Color.WHITE }
            var seenDepth = -1
            val sr = shade().apply { depth = 3 }

            reflective.pathShade(world(stubTracer(Color.WHITE) { seenDepth = it }), sr)

            seenDepth shouldBe 4
        }

        "path shade returns black when the world has no tracer to recurse through" {
            val reflective = Reflective(Color.WHITE, 0.0, 0.0).apply { kr = 1.0; cr = Color.WHITE }

            val result = reflective.pathShade(world(theTracer = null), shade())

            result shouldBeApprox Color.BLACK
        }
    })
