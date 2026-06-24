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
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.shouldBeApprox
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.world.IWorld

/**
 * Tests [Emissive.globalShade] (TASK-48, Suffern ch. 26, Listing 26.6). The hybrid global tracer must
 * not count the light twice (Fig 26.11): an emitter returns its radiance `le = ce * ls` from the front
 * face on the primary ray (`depth == 0`) and on deep indirect bounces (`depth >= 2`), but is forced to
 * black on the *first* indirect bounce (`depth == 1`), because the diffuse surface that spawned that
 * bounce already sampled this light directly. As with the other shades it is also black when hit from
 * behind the emitting face.
 */
internal class EmissiveGlobalShadeTest :
    StringSpec({

        fun shade(
            normal: Normal,
            direction: Vector3D,
            atDepth: Int,
        ): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D.ORIGIN, direction)
                override val hitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = atDepth
                override val material: IMaterial? = null
                override var normal: Normal = normal
                override var t: Double = 0.0
                override var geometricObject: IGeometricObject? = null
            }

        val world: IWorld =
            object : IWorld {
                override var tracer: Tracer? = null
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

                override fun shouldStopRecursion(depth: Int): Boolean = true
            }

        // Ceiling panel facing down; an upward ray hits its emitting (front) side.
        val frontHit = Vector3D(0.0, 1.0, 0.0)
        val ce = Color(0.2, 0.4, 0.6)
        val ls = 3.0
        val le = ce * ls

        "emits its radiance when the camera ray hits the front face at depth 0" {
            val emissive = Emissive(ce = ce, ls = ls)
            val sr = shade(Normal.DOWN, frontHit, atDepth = 0)

            val result = emissive.globalShade(world, sr)

            result shouldBeApprox le
        }

        "is black on the first indirect bounce (depth 1) to avoid counting direct light twice" {
            val emissive = Emissive(ce = ce, ls = ls)
            val sr = shade(Normal.DOWN, frontHit, atDepth = 1)

            val result = emissive.globalShade(world, sr)

            result shouldBeApprox Color.BLACK
        }

        "emits again on a deeper indirect bounce (depth 2) not covered by direct sampling" {
            val emissive = Emissive(ce = ce, ls = ls)
            val sr = shade(Normal.DOWN, frontHit, atDepth = 2)

            val result = emissive.globalShade(world, sr)

            result shouldBeApprox le
        }

        "is black when hit from behind the emitting face at depth 0" {
            val emissive = Emissive(ce = ce, ls = ls)
            val sr = shade(Normal.DOWN, Vector3D(0.0, -1.0, 0.0), atDepth = 0)

            val result = emissive.globalShade(world, sr)

            result shouldBeApprox Color.BLACK
        }
    })
