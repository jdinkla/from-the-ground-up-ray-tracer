package net.dinkla.raytracer.materials

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.Fixture.Ex
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
 * Tests [Matte.pathShade] (TASK-20, Suffern ch. 26). The shade importance-samples an indirect
 * direction with the cosine-weighted diffuse BRDF, traces the reflected ray one level deeper, and
 * weights the incoming radiance by `f * (n . wi) / pdf`.
 *
 * Because the diffuse BRDF is cosine-weighted, `pdf = (n . wi) / PI` and `f = cd * kd / PI`, so the
 * per-sample weight collapses analytically:
 *
 *   f * (n . wi) / pdf = (cd*kd/PI) * (n . wi) * PI / (n . wi) = cd * kd
 *
 * The result must therefore equal `cd * kd * incoming` regardless of the sampled direction — a
 * direction-independent invariant that makes the test deterministic despite the random sampling.
 */
internal class MattePathShadeTest :
    StringSpec({

        fun shade(normal: Normal): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D(0.0, 0.0, 1.0), Vector3D(0.0, 0.0, -1.0))
                override val hitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = 0
                override val material: IMaterial? = null
                override var normal: Normal = normal
                override var t: Double = 1.0
                override var geometricObject: IGeometricObject? = null
            }

        fun worldReturning(
            incoming: Color,
            recordedRay: (Ray) -> Unit = {},
        ): IWorld =
            object : IWorld {
                override var tracer: Tracer? =
                    object : Tracer {
                        override fun trace(
                            ray: Ray,
                            depth: Int,
                        ): Color {
                            recordedRay(ray)
                            return incoming
                        }
                    }
                override val lights: List<Light> = emptyList()
                override val ambientLight: Ambient = Ambient()
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

        "path shade weights incoming radiance by the diffuse reflectance cd*kd" {
            val matte = Matte(Ex.cd, Ex.ka, Ex.kd)
            val incoming = Color(0.4, 0.6, 0.8)
            val world = worldReturning(incoming)

            val result = matte.pathShade(world, shade(Normal.UP))

            // f * (n.wi) / pdf == cd*kd, so the result is cd*kd*incoming componentwise.
            result shouldBeApprox (Ex.cd * Ex.kd * incoming)
        }

        "path shade traces the reflected ray one recursion level deeper" {
            val matte = Matte(Ex.cd, Ex.ka, Ex.kd)
            var seenDepth = -1
            val sr = shade(Normal.UP).apply { depth = 3 }
            val world =
                object : IWorld {
                    override var tracer: Tracer? =
                        object : Tracer {
                            override fun trace(
                                ray: Ray,
                                depth: Int,
                            ): Color {
                                seenDepth = depth
                                return Color.WHITE
                            }
                        }
                    override val lights: List<Light> = emptyList()
                    override val ambientLight: Ambient = Ambient()
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

            matte.pathShade(world, sr)

            seenDepth shouldBe 4
        }

        "path shade returns black when the world has no tracer to recurse through" {
            val matte = Matte(Ex.cd, Ex.ka, Ex.kd)
            val world = worldReturning(Color.WHITE)
            world.tracer = null

            val result = matte.pathShade(world, shade(Normal.UP))

            result shouldBeApprox Color.BLACK
        }
    })
