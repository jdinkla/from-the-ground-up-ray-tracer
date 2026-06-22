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
 * Tests [Emissive.pathShade] (TASK-20): an emissive surface is the path tracer's light source. It
 * emits its radiance `le = ce * ls` when the ray hits its front (emitting) face — `-(normal) .
 * ray.direction > 0` — and is black from behind, the same front-face test as `areaLightShade`.
 */
internal class EmissivePathShadeTest :
    StringSpec({

        fun shade(
            normal: Normal,
            direction: Vector3D,
        ): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D.ORIGIN, direction)
                override val hitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = 0
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

        "emits its radiance when hit on the front face" {
            // Ceiling panel facing down; an upward ray hits its emitting side.
            val emissive = Emissive(ce = Color(0.2, 0.4, 0.6), ls = 3.0)
            val sr = shade(Normal.DOWN, Vector3D(0.0, 1.0, 0.0))

            val result = emissive.pathShade(world, sr)

            result shouldBeApprox (Color(0.2, 0.4, 0.6) * 3.0)
        }

        "is black when hit from behind the emitting face" {
            val emissive = Emissive(ce = Color(0.2, 0.4, 0.6), ls = 3.0)
            // Same downward-facing panel but the ray travels downward, hitting the back side.
            val sr = shade(Normal.DOWN, Vector3D(0.0, -1.0, 0.0))

            val result = emissive.pathShade(world, sr)

            result shouldBeApprox Color.BLACK
        }
    })
