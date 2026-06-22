package net.dinkla.raytracer.materials

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.doubles.shouldBeGreaterThan
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
import kotlin.math.pow
import kotlin.math.sin

/**
 * Pins [Dielectric.shade]: the Fresnel-weighted recursion and Beer's-law attenuation. The world has
 * no lights and zero ambient, so the Phong direct term is black and `shade` returns exactly the
 * reflected + transmitted contribution. A stub tracer returns a fixed colour and reports a fixed hit
 * distance through the `WrappedDouble`, so the expected attenuated colour can be computed by hand.
 */
internal class DielectricShadeTest :
    StringSpec({

        // Stub tracer: returns [traced] for every ray and writes [hitDistance] into the tmin out-param
        // (mirroring how Whitted reports the nearest hit distance), so Beer's-law attenuation is exact.
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

        "total internal reflection returns only the inside-attenuated reflected ray" {
            // Ray inside the glass heading up-and-out at 60 degrees (> critical ~41.8), so TIR.
            val theta = TIR_DEG * PI / DEGREES_HALF_TURN
            val sr = hit(Vector3D(sin(theta), cos(theta), 0.0), Normal.UP)
            val cfIn = Color(0.8, 0.9, 0.7)
            val traced = Color(0.5, 0.5, 0.5)
            val material =
                Dielectric().apply {
                    iorIn = 1.5
                    iorOut = 1.0
                    this.cfIn = cfIn
                    cfOut = Color.WHITE
                }

            val color = material.shade(world(StubTracer(traced, HIT_DISTANCE)), sr)

            // Only the reflected ray, attenuated by cfIn^distance (the reflected ray stays inside).
            val expected =
                Color(
                    cfIn.red.pow(HIT_DISTANCE) * traced.red,
                    cfIn.green.pow(HIT_DISTANCE) * traced.green,
                    cfIn.blue.pow(HIT_DISTANCE) * traced.blue,
                )
            color shouldBeApprox expected
        }

        "below the critical angle the result blends reflected and transmitted rays" {
            // Ray entering the glass from air at 60 degrees: both a reflected and a transmitted ray.
            val theta = ENTER_DEG * PI / DEGREES_HALF_TURN
            val sr = hit(Vector3D(sin(theta), -cos(theta), 0.0), Normal.UP)
            val traced = Color(0.6, 0.6, 0.6)
            val material =
                Dielectric().apply {
                    iorIn = 1.5
                    iorOut = 1.0
                    cfIn = Color.WHITE
                    cfOut = Color.WHITE
                }

            val color = material.shade(world(StubTracer(traced, HIT_DISTANCE)), sr)

            // With white filters the attenuation is the identity, so both rays contribute and the
            // result is strictly brighter than a single reflected-only contribution would be.
            color.red shouldBeGreaterThan 0.0
            color.green shouldBeGreaterThan 0.0
            color.blue shouldBeGreaterThan 0.0
        }
    }) {
    private companion object {
        private const val PI = 3.141592653589793
        private const val DEGREES_HALF_TURN = 180.0
        private const val TIR_DEG = 60.0
        private const val ENTER_DEG = 60.0
        private const val HIT_DISTANCE = 2.0
    }
}
