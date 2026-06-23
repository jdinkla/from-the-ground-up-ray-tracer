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
import kotlin.math.cos
import kotlin.math.sin

/**
 * Pins [Transparent.shade]. The world has no lights and zero ambient, so the Phong direct term is
 * black and `shade` returns exactly the reflected/transmitted contribution.
 *
 *  - **Total internal reflection** (steep ray exiting the denser medium): `shade` adds only the
 *    reflected ray's traced colour, unscaled — so a perfect-mirror setup returns the traced colour.
 *  - **Below the critical angle** (ray entering the medium) with `kr = 0`: only the transmitted ray
 *    contributes, and the BTDF terms collapse to `(kt / ior^2) * tracedColour`.
 */
internal class TransparentTest :
    StringSpec({

        val ior = 1.5
        val theta = 60.0 * PI / DEGREES_HALF_TURN

        // Ray heading up-and-out of the denser medium at 60deg (> critical) => TIR.
        val exitingRay = Vector3D(sin(theta), cos(theta), 0.0)
        // Ray entering the denser medium at 60deg => a transmitted ray exists.
        val enteringRay = Vector3D(sin(theta), -cos(theta), 0.0)

        fun shade(rayDir: Vector3D): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D.ORIGIN, rayDir)
                override val hitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = 0
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

        fun stubTracer(traced: Color): Tracer =
            object : Tracer {
                override fun trace(
                    ray: Ray,
                    depth: Int,
                ): Color = traced
            }

        fun transparent(): Transparent =
            Transparent(color = Color.WHITE, ka = 0.0, kd = 0.0, kt = 1.0, ior = ior, kr = 0.0)

        "total internal reflection adds the reflected ray's traced colour unscaled" {
            val traced = Color(0.3, 0.5, 0.7)

            val result = transparent().shade(world(stubTracer(traced)), shade(exitingRay))

            result shouldBeApprox traced
        }

        "total internal reflection contributes black when the world has no tracer" {
            val result = transparent().shade(world(theTracer = null), shade(exitingRay))

            result shouldBeApprox Color.BLACK
        }

        "below the critical angle the transmitted ray contributes kt/ior^2 of the traced colour" {
            val traced = Color(0.9, 0.9, 0.9)

            val result = transparent().shade(world(stubTracer(traced)), shade(enteringRay))

            result shouldBeApprox (traced * (1.0 / (ior * ior)))
        }

        "below the critical angle the transmitted ray defaults to white with no tracer" {
            val result = transparent().shade(world(theTracer = null), shade(enteringRay))

            result shouldBeApprox (Color.WHITE * (1.0 / (ior * ior)))
        }

        "equality and hashCode follow the base Phong, the reflective BRDF and the transmitter" {
            val a = Transparent(Ex.cd, Ex.ka, Ex.kd, Ex.exp, Ex.ks, Ex.cs, kt = Ex.kt, ior = ior)
            val b = Transparent(Ex.cd, Ex.ka, Ex.kd, Ex.exp, Ex.ks, Ex.cs, kt = Ex.kt, ior = ior)

            a shouldBe b
            a.hashCode() shouldBe b.hashCode()
            a shouldNotBe Transparent(Ex.cd, Ex.ka, Ex.kd, Ex.exp, Ex.ks, Ex.cs, kt = Ex.kt + 0.01, ior = ior)
            a shouldNotBe null
            (a.equals("nope")) shouldBe false
        }

        // Each case isolates one clause of the equals && chain so it becomes the deciding (false)
        // term: diffuse (kd), ambient (ka), reflective (kr) and specular (exp) BRDFs respectively. The
        // earlier clauses stay equal, so equality short-circuits exactly on the intended field.
        "Transparents differing only in the diffuse coefficient are not equal" {
            val a = Transparent(Ex.cd, Ex.ka, Ex.kd, Ex.exp, Ex.ks, Ex.cs, kt = Ex.kt, ior = ior)
            val b = Transparent(Ex.cd, Ex.ka, Ex.kd + 0.1, Ex.exp, Ex.ks, Ex.cs, kt = Ex.kt, ior = ior)

            a shouldNotBe b
        }

        "Transparents differing only in the ambient coefficient are not equal" {
            val a = Transparent(Ex.cd, Ex.ka, Ex.kd, Ex.exp, Ex.ks, Ex.cs, kt = Ex.kt, ior = ior)
            val b = Transparent(Ex.cd, Ex.ka + 0.1, Ex.kd, Ex.exp, Ex.ks, Ex.cs, kt = Ex.kt, ior = ior)

            a shouldNotBe b
        }

        "Transparents differing only in the reflection coefficient are not equal" {
            val a = Transparent(Ex.cd, Ex.ka, Ex.kd, Ex.exp, Ex.ks, Ex.cs, kt = Ex.kt, ior = ior, kr = 0.0)
            val b = Transparent(Ex.cd, Ex.ka, Ex.kd, Ex.exp, Ex.ks, Ex.cs, kt = Ex.kt, ior = ior, kr = 0.5)

            a shouldNotBe b
        }

        "Transparents differing only in the specular exponent are not equal" {
            val a = Transparent(Ex.cd, Ex.ka, Ex.kd, Ex.exp, Ex.ks, Ex.cs, kt = Ex.kt, ior = ior)
            val b = Transparent(Ex.cd, Ex.ka, Ex.kd, Ex.exp + 1.0, Ex.ks, Ex.cs, kt = Ex.kt, ior = ior)

            a shouldNotBe b
        }

        "toString names the material" {
            transparent().toString() shouldContain "Transparent"
        }
    }) {
    private companion object {
        private const val PI = 3.141592653589793
        private const val DEGREES_HALF_TURN = 180.0
    }
}
