package net.dinkla.raytracer.materials

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
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
 * Covers [Emissive]: it self-emits `ce * ls` and is only visible from the emitting (front) face in
 * the [areaLightShade] path — the back face is black. Also pins its value-type contract.
 */
internal class EmissiveTest :
    StringSpec({

        val le = Ex.cd * Ex.kt

        // rayDir is the *incident* ray direction; the front face is the one the ray hits head-on.
        fun shade(rayDir: Vector3D): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D(0.0, 0.0, 5.0), rayDir)
                override val hitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = 0
                override val material: IMaterial? = null
                override var normal: Normal = Normal.UP
                override var t: Double = 0.0
                override var geometricObject: IGeometricObject? = null
            }

        val world =
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

        "shade always returns the emitted radiance regardless of viewing direction" {
            val emissive = Emissive(Ex.cd, Ex.kt)

            emissive.shade(world, shade(Vector3D(0.0, 1.0, 0.0))) shouldBeApprox le
        }

        "area light shade emits from the front face" {
            val emissive = Emissive(Ex.cd, Ex.kt)

            // normal up, ray travelling down: -normal . dir = 1 > 0 => front => emits.
            emissive.areaLightShade(world, shade(Vector3D(0.0, -1.0, 0.0))) shouldBeApprox le
        }

        "area light shade is black from the back face" {
            val emissive = Emissive(Ex.cd, Ex.kt)

            // ray travelling up while the normal points up: -normal . dir = -1 < 0 => back => black.
            emissive.areaLightShade(world, shade(Vector3D(0.0, 1.0, 0.0))) shouldBeApprox Color.BLACK
        }

        "getLe returns the emitted radiance" {
            Emissive(Ex.cd, Ex.kt).getLe(shade(Vector3D(0.0, -1.0, 0.0))) shouldBeApprox le
        }

        "equality and hashCode follow colour and scale" {
            val a = Emissive(Ex.cd, Ex.kt)
            val b = Emissive(Ex.cd, Ex.kt)

            a shouldBe b
            a.hashCode() shouldBe b.hashCode()
            a shouldNotBe Emissive(Ex.cs, Ex.kt)
            a shouldNotBe Emissive(Ex.cd, Ex.kt + 0.1)
            a shouldNotBe null
            (a.equals("nope")) shouldBe false
        }

        "toString names the material" {
            Emissive(Ex.cd, Ex.kt).toString() shouldContain "Emissive"
        }
    })
