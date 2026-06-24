package net.dinkla.raytracer.materials

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
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
 * Tests [Transparent.pathShade] (TASK-47, Suffern ch. 28 / ch. 26). In the path tracer a transparent
 * surface carries no direct (Phong) term — it spawns the perfect-specular *reflected* ray and the
 * perfect-transmitter *transmitted* ray one recursion level deeper, exactly the global-illumination
 * part of the Whitted [Transparent.shade] minus `super.shade`. Total internal reflection (no
 * transmitted ray, all energy reflects) is handled via the BTDF [net.dinkla.raytracer.btdf.PerfectTransmitter.isTir].
 *
 * The geometry below sends the incident ray straight down onto an up-facing surface, so `wo` is the
 * normal, the reflected direction is straight up (`n . wi = 1`) and the transmitted direction is
 * straight down (no TIR). For the reflected ray the per-sample weight collapses analytically:
 * `PerfectSpecular.sampleF` returns `color = cr * (kr / |n . wi|)`, and the shade weights it by
 * `|n . wi|`, so the reflected contribution is exactly `cr * kr * incoming`.
 */
internal class TransparentPathShadeTest :
    StringSpec({

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

        // Returns a fixed colour for every traced ray, recording the recursion depth(s) it was called at.
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

        // Returns radiance keyed by the traced ray's direction, so reflected vs transmitted bounce can
        // be told apart by what the material does with each.
        fun directionTracer(byDirection: (Vector3D) -> Color): Tracer =
            object : Tracer {
                override fun trace(
                    ray: Ray,
                    depth: Int,
                ): Color = byDirection(ray.direction)
            }

        "path shade adds the reflected term cr*kr*incoming on top of the transmitted term (no TIR)" {
            // ior=1 makes the transmitter pass the ray straight through with the incident radiance,
            // so the transmitted contribution is exactly the incoming radiance and the reflected
            // contribution is cr*kr*incoming.
            val transparent =
                Transparent().apply {
                    cd = Color.BLACK; ka = 0.0; kd = 0.0; ks = 0.0
                    kt = 1.0; ior = 1.0; kr = 0.5; cr = Color(0.2, 0.4, 0.8)
                }
            val incoming = Color(0.3, 0.6, 0.9)

            val result = transparent.pathShade(world(stubTracer(incoming)), shade())

            // reflected: cr*kr*incoming ; transmitted (ior=1, straight through): incoming.
            result shouldBeApprox (Color(0.2, 0.4, 0.8) * 0.5 * incoming + incoming)
        }

        "path shade traces both the reflected and transmitted rays one recursion level deeper" {
            val transparent =
                Transparent().apply {
                    cd = Color.BLACK; ka = 0.0; kd = 0.0; ks = 0.0
                    kt = 1.0; ior = 1.5; kr = 0.5; cr = Color.WHITE
                }
            val seenDepths = mutableListOf<Int>()
            val sr = shade().apply { depth = 2 }

            transparent.pathShade(world(stubTracer(Color.WHITE) { seenDepths.add(it) }), sr)

            // Two bounces (reflected + transmitted), both at depth 3.
            seenDepths shouldContainExactly listOf(3, 3)
        }

        "path shade spawns the reflected ray straight up and the transmitted ray straight down" {
            val transparent =
                Transparent().apply {
                    cd = Color.BLACK; ka = 0.0; kd = 0.0; ks = 0.0
                    kt = 1.0; ior = 1.0; kr = 1.0; cr = Color.WHITE
                }
            // Reflected (up) carries RED, transmitted (down) carries BLUE; both have |n.wi|=1 so the
            // weights are 1, and the result is RED + BLUE = MAGENTA. Confirms the two directions.
            val tracer =
                directionTracer { dir ->
                    if (dir.y > 0.0) Color.RED else Color.BLUE
                }

            val result = transparent.pathShade(world(tracer), shade())

            result shouldBeApprox (Color.RED + Color.BLUE)
        }

        "path shade reflects all energy and traces only the reflected ray under total internal reflection" {
            // A ray inside a denser medium hitting the boundary at a grazing angle is totally internally
            // reflected. With the normal pointing up but the ray travelling up-and-sideways at a shallow
            // angle and ior < 1 (denser inside), isTir is true: only the reflected ray is traced.
            val transparent =
                Transparent().apply {
                    cd = Color.BLACK; ka = 0.0; kd = 0.0; ks = 0.0
                    kt = 1.0; ior = 0.6; kr = 1.0; cr = Color.WHITE
                }
            // wo points up-and-along-x at a shallow grazing angle to the up normal.
            val grazing =
                object : IShade {
                    override var ray: Ray = Ray(Point3D.ORIGIN, Vector3D(-0.99, -0.1411, 0.0))
                    override val hitPoint: Point3D = Point3D.ORIGIN
                    override var depth: Int = 0
                    override val material: IMaterial? = null
                    override var normal: Normal = Normal.UP
                    override var t: Double = 0.0
                    override var geometricObject: IGeometricObject? = null
                }
            var calls = 0

            val result = transparent.pathShade(world(stubTracer(Color(0.5, 0.5, 0.5)) { calls++ }), grazing)

            // Under TIR the shade adds the reflected radiance directly (l += cr) — a single bounce.
            calls shouldBe 1
            result shouldBeApprox Color(0.5, 0.5, 0.5)
        }

        "path shade returns black when the world has no tracer to recurse through" {
            val transparent =
                Transparent().apply {
                    cd = Color.BLACK; ka = 0.0; kd = 0.0; ks = 0.0
                    kt = 1.0; ior = 1.5; kr = 0.5; cr = Color.WHITE
                }

            val result = transparent.pathShade(world(theTracer = null), shade())

            result shouldBeApprox Color.BLACK
        }
    })
