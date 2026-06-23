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
import net.dinkla.raytracer.textures.ConstantColor
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.world.IWorld

/**
 * Covers [SvEmissive]: like [Emissive] but its emitted radiance is read from a [net.dinkla.raytracer
 * .textures.Texture] at the hit point. Visible only from the front face in [areaLightShade].
 */
internal class SvEmissiveTest :
    StringSpec({

        val le = Ex.cd * Ex.kt

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

        "shade returns the textured radiance scaled by ls" {
            val emissive = SvEmissive(ConstantColor(Ex.cd), Ex.kt)

            emissive.shade(world, shade(Vector3D(0.0, 1.0, 0.0))) shouldBeApprox le
        }

        "area light shade emits from the front face" {
            val emissive = SvEmissive(ConstantColor(Ex.cd), Ex.kt)

            emissive.areaLightShade(world, shade(Vector3D(0.0, -1.0, 0.0))) shouldBeApprox le
        }

        "area light shade is black from the back face" {
            val emissive = SvEmissive(ConstantColor(Ex.cd), Ex.kt)

            emissive.areaLightShade(world, shade(Vector3D(0.0, 1.0, 0.0))) shouldBeApprox Color.BLACK
        }

        "getLe returns the textured radiance" {
            SvEmissive(ConstantColor(Ex.cd), Ex.kt).getLe(shade(Vector3D(0.0, -1.0, 0.0))) shouldBeApprox le
        }

        "equality and hashCode follow texture and scale" {
            val a = SvEmissive(ConstantColor(Ex.cd), Ex.kt)
            val b = SvEmissive(ConstantColor(Ex.cd), Ex.kt)

            a shouldBe b
            a.hashCode() shouldBe b.hashCode()
            a shouldNotBe SvEmissive(ConstantColor(Ex.cs), Ex.kt)
            a shouldNotBe SvEmissive(ConstantColor(Ex.cd), Ex.kt + 0.1)
            a shouldNotBe null
            (a.equals("nope")) shouldBe false
        }

        "toString names the material" {
            SvEmissive(ConstantColor(Ex.cd), Ex.kt).toString() shouldContain "SvEmissive"
        }
    })
