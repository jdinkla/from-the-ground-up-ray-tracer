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
 * Tests [Reflective.globalShade] (TASK-48, Suffern ch. 26, Listing 26.8). A perfect mirror carries no
 * direct light term in the hybrid global tracer — it only forwards the specular bounce — so its
 * global shade equals its [Reflective.pathShade] at every depth (including the first hit, where
 * [Matte] would add a direct term). The reflected weight collapses to `cr * kr * incoming`, exactly as
 * pinned in [ReflectivePathShadeTest].
 */
internal class ReflectiveGlobalShadeTest :
    StringSpec({

        // Incident ray hits the up-facing surface at 45 degrees so the reflected direction points up
        // and away (n . wi > 0).
        fun shade(atDepth: Int): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D(-1.0, 1.0, 0.0), Vector3D(1.0, -1.0, 0.0))
                override val hitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = atDepth
                override val material: IMaterial? = null
                override var normal: Normal = Normal.UP
                override var t: Double = 0.0
                override var geometricObject: IGeometricObject? = null
            }

        fun world(theTracer: Tracer?): IWorld =
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

        "global shade weights the reflected radiance by the mirror reflectance cr*kr at the first hit" {
            val reflective = Reflective(Color.WHITE, 0.0, 0.0).apply { kr = 0.4; cr = Color(0.2, 0.5, 0.9) }
            val incoming = Color(0.3, 0.6, 0.8)

            val result = reflective.globalShade(world(stubTracer(incoming)), shade(atDepth = 0))

            // No direct term is added at depth 0; sample.color * (n.wi) == cr*kr.
            result shouldBeApprox (Color(0.2, 0.5, 0.9) * 0.4 * incoming)
        }

        "global shade behaves the same on a deeper bounce" {
            val reflective = Reflective(Color.WHITE, 0.0, 0.0).apply { kr = 1.0; cr = Color.WHITE }
            val incoming = Color(0.3, 0.5, 0.7)

            val result = reflective.globalShade(world(stubTracer(incoming)), shade(atDepth = 2))

            result shouldBeApprox incoming
        }

        "global shade traces the reflected ray one recursion level deeper" {
            val reflective = Reflective(Color.WHITE, 0.0, 0.0).apply { kr = 1.0; cr = Color.WHITE }
            var seenDepth = -1
            val sr = shade(atDepth = 3)

            reflective.globalShade(world(stubTracer(Color.WHITE) { seenDepth = it }), sr)

            seenDepth shouldBe 4
        }

        "global shade returns black when the world has no tracer to recurse through" {
            val reflective = Reflective(Color.WHITE, 0.0, 0.0).apply { kr = 1.0; cr = Color.WHITE }

            val result = reflective.globalShade(world(theTracer = null), shade(atDepth = 0))

            result shouldBeApprox Color.BLACK
        }
    })
