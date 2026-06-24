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
import net.dinkla.raytracer.math.WrappedDouble
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.shouldBeApprox
import net.dinkla.raytracer.tracers.Tracer
import net.dinkla.raytracer.world.IWorld

/**
 * Tests [Dielectric.pathShade] (TASK-47, Suffern ch. 28 §28.9). In the path tracer a dielectric
 * surface carries no direct (Phong) term — it spawns the Fresnel-weighted *reflected* and
 * *transmitted* rays one recursion level deeper, handles total internal reflection (no transmitted
 * ray, all energy reflects), and applies Beer's-law colored attenuation (`cfIn`/`cfOut` raised to the
 * traced path length). This is exactly the global-illumination part of the Whitted [Dielectric.shade]
 * minus `super.shade` — the same `fresnelContribution` the Whitted path already trusts.
 *
 * The geometry below sends the incident ray straight down onto an up-facing surface (normal incidence,
 * `n . wi = n . wt = 1`), so the per-direction geometry weights are 1 and the Fresnel split is the
 * normal-incidence reflectance `kr = ((eta-1)/(eta+1))^2`. With `eta = iorIn/iorOut = 1.5` that is
 * `0.04`, so 4% reflects (`KR`). The transmitted ray additionally carries the radiance-compression
 * factor `1/eta^2` (Suffern's `FresnelTransmitter.sampleF` returns `kt / eta^2 / |n . wt|`), so the
 * transmitted weight is `kt / eta^2 = 0.96 / 2.25` (`KT_OVER_ETA_SQ`), not just `kt`. These are the
 * exact weights the Whitted `shade` uses — `pathShade` reuses the same `fresnelContribution`.
 */
internal class DielectricPathShadeTest :
    StringSpec({

        // Normal-incidence reflectance for eta = iorIn/iorOut = 1.5: ((1.5-1)/(1.5+1))^2 = 0.04.
        val kr = 0.04
        // Transmitted weight at normal incidence: kt / eta^2 = (1 - kr) / 1.5^2 = 0.96 / 2.25.
        val ktOverEtaSq = (1.0 - kr) / (1.5 * 1.5)

        // Incident ray straight down onto an up-facing surface: wo = -ray.direction = the normal.
        fun shade(): IShade =
            object : IShade {
                override var ray: Ray = Ray(Point3D(0.0, 1.0, 0.0), Vector3D(0.0, -1.0, 0.0))
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

        // Reports a fixed colour and a fixed hit distance (through the WrappedDouble overload that
        // Dielectric uses for Beer's-law attenuation), recording the depth(s) it was traced at.
        fun stubTracer(
            traced: Color,
            distance: Double = 0.0,
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

                override fun trace(
                    ray: Ray,
                    tmin: WrappedDouble,
                    depth: Int,
                ): Color {
                    recordDepth(depth)
                    tmin.value = distance
                    return traced
                }
            }

        // Returns radiance keyed by the traced ray's direction, with zero path length (no attenuation).
        fun directionTracer(byDirection: (Vector3D) -> Color): Tracer =
            object : Tracer {
                override fun trace(
                    ray: Ray,
                    depth: Int,
                ): Color = byDirection(ray.direction)

                override fun trace(
                    ray: Ray,
                    tmin: WrappedDouble,
                    depth: Int,
                ): Color {
                    tmin.value = 0.0
                    return byDirection(ray.direction)
                }
            }

        "path shade splits incoming radiance by the Fresnel reflectance and transmittance at normal incidence" {
            // Clear glass (white filters), eta = 1.5 => 4% reflects, 96% transmits. With distance 0 there
            // is no Beer's-law attenuation, so the result is just the Fresnel-weighted sum.
            val dielectric =
                Dielectric().apply {
                    cd = Color.BLACK; ka = 0.0; kd = 0.0; ks = 0.0
                    iorIn = 1.5; iorOut = 1.0
                    cfIn = Color.WHITE; cfOut = Color.WHITE
                }
            val incoming = Color(0.4, 0.6, 0.8)

            val result = dielectric.pathShade(world(stubTracer(incoming, distance = 0.0)), shade())

            // Both rays carry `incoming`: reflected weighted by kr, transmitted by kt/eta^2.
            result shouldBeApprox (incoming * kr + incoming * ktOverEtaSq)
        }

        "path shade attenuates the transmitted ray by the inside filter raised to its path length (Beer's law)" {
            // Reflected ray sees no colour (RED filter pow 0 == white but reflected carries black here),
            // transmitted ray carries white over a path of length 2 inside a coloured medium: cfIn^2.
            val cfIn = Color(0.8, 0.5, 0.2)
            val dielectric =
                Dielectric().apply {
                    cd = Color.BLACK; ka = 0.0; kd = 0.0; ks = 0.0
                    iorIn = 1.5; iorOut = 1.0
                    this.cfIn = cfIn; cfOut = Color.WHITE
                }
            // Only the transmitted (downward) ray carries radiance; reflected (upward) carries black.
            val tracer =
                object : Tracer {
                    override fun trace(
                        ray: Ray,
                        depth: Int,
                    ): Color = Color.BLACK

                    override fun trace(
                        ray: Ray,
                        tmin: WrappedDouble,
                        depth: Int,
                    ): Color {
                        tmin.value = 2.0
                        return if (ray.direction.y < 0.0) Color.WHITE else Color.BLACK
                    }
                }

            val result = dielectric.pathShade(world(tracer), shade())

            // transmitted: cfIn^2 * white * (kt/eta^2) * |n.wt|(1) ; reflected contributes black.
            result shouldBeApprox (cfIn.pow(2.0) * ktOverEtaSq)
        }

        "path shade traces both the reflected and transmitted rays one recursion level deeper" {
            val dielectric =
                Dielectric().apply {
                    cd = Color.BLACK; ka = 0.0; kd = 0.0; ks = 0.0
                    iorIn = 1.5; iorOut = 1.0
                    cfIn = Color.WHITE; cfOut = Color.WHITE
                }
            val seenDepths = mutableListOf<Int>()
            val sr = shade().apply { depth = 4 }

            dielectric.pathShade(world(stubTracer(Color.WHITE, distance = 0.0) { seenDepths.add(it) }), sr)

            // Two bounces, both one level deeper than the hit depth.
            seenDepths shouldBe listOf(5, 5)
        }

        "path shade reflects all energy and traces only the reflected ray under total internal reflection" {
            // Ray inside the denser medium hitting the boundary at a grazing angle: total internal
            // reflection, so there is no transmitted ray and only the reflected ray is traced.
            val dielectric =
                Dielectric().apply {
                    cd = Color.BLACK; ka = 0.0; kd = 0.0; ks = 0.0
                    iorIn = 1.5; iorOut = 1.0
                    cfIn = Color.WHITE; cfOut = Color.WHITE
                }
            val grazing =
                object : IShade {
                    // wo = -ray.direction points up-and-along-x at a shallow angle; normal points down
                    // (the inside of the medium faces up here), giving cosThetaI small and eta = 1.5.
                    override var ray: Ray = Ray(Point3D.ORIGIN, Vector3D(0.99, 0.1411, 0.0))
                    override val hitPoint: Point3D = Point3D.ORIGIN
                    override var depth: Int = 0
                    override val material: IMaterial? = null
                    override var normal: Normal = Normal.UP
                    override var t: Double = 0.0
                    override var geometricObject: IGeometricObject? = null
                }
            var calls = 0

            val result =
                dielectric.pathShade(
                    world(stubTracer(Color(0.5, 0.5, 0.5), distance = 0.0) { calls++ }),
                    grazing,
                )

            // Single bounce (reflected only), unattenuated white filters => the traced radiance.
            calls shouldBe 1
            result shouldBeApprox Color(0.5, 0.5, 0.5)
        }

        "path shade spawns the reflected ray straight up and the transmitted ray straight down" {
            val dielectric =
                Dielectric().apply {
                    cd = Color.BLACK; ka = 0.0; kd = 0.0; ks = 0.0
                    iorIn = 1.5; iorOut = 1.0
                    cfIn = Color.WHITE; cfOut = Color.WHITE
                }
            // Reflected (up) and transmitted (down) both carry white; the split is kr + kt/eta^2.
            val tracer = directionTracer { _ -> Color.WHITE }

            val result = dielectric.pathShade(world(tracer), shade())

            // Reflected weight kr plus transmitted weight kt/eta^2, both over white.
            result shouldBeApprox (Color.WHITE * (kr + ktOverEtaSq))
        }

        "path shade returns black when the world has no tracer to recurse through" {
            val dielectric =
                Dielectric().apply {
                    cd = Color.BLACK; ka = 0.0; kd = 0.0; ks = 0.0
                    iorIn = 1.5; iorOut = 1.0
                    cfIn = Color.WHITE; cfOut = Color.WHITE
                }

            val result = dielectric.pathShade(world(theTracer = null), shade())

            result shouldBeApprox Color.BLACK
        }
    })
