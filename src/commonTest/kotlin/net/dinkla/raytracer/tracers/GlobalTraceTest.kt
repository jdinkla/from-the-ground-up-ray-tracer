package net.dinkla.raytracer.tracers

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.hits.Shade
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.materials.IMaterial
import net.dinkla.raytracer.math.BBox
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.shouldBeApprox
import net.dinkla.raytracer.world.IWorld

/**
 * Tests the [GlobalTrace] tracer (TASK-48, Suffern ch. 26). Like [PathTrace] it returns black past the
 * recursion bound, the background colour on a miss, and otherwise the hit material's shade — but it
 * calls `globalShade` (the hybrid direct-plus-indirect shade) instead of `pathShade`, and it records
 * the recursion depth into the [Shade] so the materials can apply the radiance-flow rules (Fig 26.11).
 * At the primary level (`depth == 0`) it averages several independent paths per ray; deeper it traces a
 * single bounce.
 */
internal class GlobalTraceTest :
    StringSpec({

        // A material that returns a fixed colour from globalShade only; the other shades return a
        // sentinel so the test fails loudly if the tracer ever calls the wrong one.
        fun globalMaterial(
            color: Color,
            onGlobalShade: (IShade) -> Unit = {},
        ): IMaterial =
            object : IMaterial {
                override fun shade(
                    world: IWorld,
                    sr: IShade,
                ): Color = Color.RED

                override fun areaLightShade(
                    world: IWorld,
                    sr: IShade,
                ): Color = Color.RED

                override fun pathShade(
                    world: IWorld,
                    sr: IShade,
                ): Color = Color.RED

                override fun globalShade(
                    world: IWorld,
                    sr: IShade,
                ): Color {
                    onGlobalShade(sr)
                    return color
                }

                override fun getLe(sr: IShade): Color = color
            }

        fun objectWith(material: IMaterial?): IGeometricObject =
            object : IGeometricObject {
                override var isShadows: Boolean = false
                override var boundingBox: BBox = BBox(Point3D.ORIGIN, Point3D.ORIGIN)
                override var material: IMaterial? = material

                override fun initialize() = Unit

                override fun hit(
                    ray: Ray,
                    sr: IHit,
                ): Boolean = false
            }

        fun world(
            doesHit: Boolean,
            stop: Boolean,
            material: IMaterial? = null,
            background: Color = Color.BLUE,
        ): IWorld =
            object : IWorld {
                override var tracer: Tracer? = null
                override val lights: List<Light> = emptyList()
                override val ambientLight: Ambient = Ambient()
                override var backgroundColor: Color = background

                override fun hit(
                    ray: Ray,
                    sr: IHit,
                ): Boolean {
                    if (doesHit && sr is Shade) {
                        sr.t = 1.0
                        sr.normal = Normal.UP
                        sr.geometricObject = objectWith(material)
                    }
                    return doesHit
                }

                override fun inShadow(
                    ray: Ray,
                    sr: IShade,
                    d: Double,
                ): Boolean = false

                override fun shouldStopRecursion(depth: Int): Boolean = stop
            }

        val ray = Ray(Point3D(0.0, 0.0, 1.0), Vector3D(0.0, 0.0, -1.0))

        "returns black past the recursion bound" {
            val tracer = GlobalTrace(world(doesHit = true, stop = true))

            val color = tracer.trace(ray, depth = 1)

            color shouldBeApprox Color.BLACK
        }

        "returns the background colour when the ray misses" {
            val tracer = GlobalTrace(world(doesHit = false, stop = false, background = Color.BLUE))

            val color = tracer.trace(ray, depth = 1)

            color shouldBeApprox Color.BLUE
        }

        "returns the hit material's global shade on a deeper bounce" {
            val green = globalMaterial(Color.GREEN)
            val tracer = GlobalTrace(world(doesHit = true, stop = false, material = green))

            val color = tracer.trace(ray, depth = 1)

            color shouldBeApprox Color.GREEN
        }

        "records the recursion depth into the shade so the radiance-flow rules can read it" {
            var seenDepth = -1
            val green = globalMaterial(Color.GREEN) { seenDepth = it.depth }
            val tracer = GlobalTrace(world(doesHit = true, stop = false, material = green))

            tracer.trace(ray, depth = 2)

            seenDepth shouldBe 2
        }

        "averages primary-ray paths to the same value when every path is identical" {
            // Every path returns GREEN, so the depth-0 average over many samples is exactly GREEN.
            val green = globalMaterial(Color.GREEN)
            val tracer = GlobalTrace(world(doesHit = true, stop = false, material = green), numSamples = 16)

            val color = tracer.trace(ray, depth = 0)

            color shouldBeApprox Color.GREEN
        }
    })
