package net.dinkla.raytracer.materials

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
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
import net.dinkla.raytracer.math.WrappedDouble
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.shouldBeApprox
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.world.IWorld
import kotlin.math.cos
import kotlin.math.sin

/**
 * Covers [Dielectric]'s value-type contract ([equals]/[hashCode]/[toString]) and its area-light path
 * ([areaLightShade] = `super.areaLightShade + fresnelContribution`). The fresnel-weighted recursion
 * itself (TIR, Beer's-law attenuation) is already pinned by [DielectricShadeTest]; here the area-light
 * branch is exercised once. The world has no lights and zero ambient, so the Phong/area-light direct
 * term is black and `areaLightShade` returns exactly the fresnel reflected/transmitted contribution.
 */
internal class DielectricTest :
    StringSpec({

        class StubTracer(
            private val traced: Color,
            private val hitDistance: Double,
        ) : Tracer {
            override fun trace(
                ray: Ray,
                depth: Int,
            ): Color = traced

            override fun trace(
                ray: Ray,
                tmin: WrappedDouble,
                depth: Int,
            ): Color {
                tmin.value = hitDistance
                return traced
            }
        }

        fun world(tracer: Tracer): IWorld =
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

        fun hit(
            incident: Vector3D,
            normal: Normal,
        ): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D.ORIGIN, incident)
                override val hitPoint: Point3D = Point3D.ORIGIN
                override val localHitPoint: Point3D = Point3D.ORIGIN
                override var depth: Int = 0
                override val material: IMaterial? = null
                override var normal: Normal = normal
                override var t: Double = 0.0
                override var geometricObject: IGeometricObject? = null
            }

        fun glass(): Dielectric =
            Dielectric().apply {
                iorIn = 1.5
                iorOut = 1.0
                cfIn = Color.WHITE
                cfOut = Color.WHITE
            }

        // area-light path -----------------------------------------------------

        "area light shade adds the fresnel reflected/transmitted contribution above the (black) direct term" {
            // Ray entering the glass from air at 60 degrees: both a reflected and a transmitted ray.
            val theta = ENTER_DEG * PI / DEGREES_HALF_TURN
            val sr = hit(Vector3D(sin(theta), -cos(theta), 0.0), Normal.UP)
            val traced = Color(0.6, 0.6, 0.6)

            val color = glass().areaLightShade(world(StubTracer(traced, HIT_DISTANCE)), sr)

            // No lights and zero ambient => the Phong direct term is black, so the whole result is the
            // fresnel contribution, which is strictly positive for this geometry with white filters.
            color.red shouldBeGreaterThan 0.0
            color.green shouldBeGreaterThan 0.0
            color.blue shouldBeGreaterThan 0.0
        }

        // value-type contract -------------------------------------------------

        "two Dielectrics with the same parameters are equal and share a hash code" {
            val a = glass()
            val b = glass()

            a shouldBe b
            a.hashCode() shouldBe b.hashCode()
        }

        "Dielectrics differing in the inside index of refraction are not equal" {
            val a = glass()
            val b = glass().apply { iorIn = 1.6 }

            a shouldNotBe b
        }

        "Dielectrics differing in the inside filter colour are not equal" {
            val a = glass()
            val b = glass().apply { cfIn = Color(0.8, 0.9, 0.7) }

            a shouldNotBe b
        }

        // The following four cases each isolate one later clause of the equals && chain so it becomes
        // the deciding (false) term: ior and cfIn match, so equality reaches and rejects on cfOut,
        // ka (ambient BRDF), kd (diffuse BRDF) and exp (specular BRDF) respectively.

        "Dielectrics differing only in the outside filter colour are not equal" {
            val a = glass()
            val b = glass().apply { cfOut = Color(0.7, 0.6, 0.5) }

            a shouldNotBe b
        }

        "Dielectrics differing only in the ambient coefficient are not equal" {
            val a = glass()
            val b = glass().apply { ka += 0.1 }

            a shouldNotBe b
        }

        "Dielectrics differing only in the diffuse coefficient are not equal" {
            val a = glass()
            val b = glass().apply { kd += 0.1 }

            a shouldNotBe b
        }

        "Dielectrics differing only in the specular exponent are not equal" {
            val a = glass()
            val b = glass().apply { exp += 1.0 }

            a shouldNotBe b
        }

        "a Dielectric is not equal to null or to a non-Dielectric value" {
            val a = glass()

            a shouldNotBe null
            (a.equals("not a material")) shouldBe false
        }

        "toString names the material and exposes the filter colours" {
            val s = glass().toString()

            s shouldContain "Dielectric"
            s shouldContain "cfIn"
        }
    }) {
    private companion object {
        private const val PI = 3.141592653589793
        private const val DEGREES_HALF_TURN = 180.0
        private const val ENTER_DEG = 60.0
        private const val HIT_DISTANCE = 2.0
    }
}
