package net.dinkla.raytracer.tracers

import io.kotest.core.spec.style.StringSpec
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
 * Tests the [PathTrace] tracer (TASK-20). The path tracer returns black past the recursion bound, the
 * background colour when a ray misses, and otherwise the hit material's `pathShade`. At the primary
 * level (`depth == 0`) it averages several independent paths per ray to converge; deeper it traces a
 * single bounce.
 */
internal class PathTraceTest :
    StringSpec({

        // A material whose pathShade returns a fixed colour, so the tracer's plumbing is observable.
        fun constantMaterial(color: Color): IMaterial =
            object : IMaterial {
                override fun shade(
                    world: IWorld,
                    sr: IShade,
                ): Color = color

                override fun areaLightShade(
                    world: IWorld,
                    sr: IShade,
                ): Color = color

                override fun pathShade(
                    world: IWorld,
                    sr: IShade,
                ): Color = color

                override fun getLe(sr: IShade): Color = color
            }

        // Minimal geometric object: only carries a material; the path tracer reads it via Shade.material.
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
                override var tracer: net.dinkla.raytracer.tracers.Tracer? = null
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
            val tracer = PathTrace(world(doesHit = true, stop = true))

            val color = tracer.trace(ray, depth = 1)

            color shouldBeApprox Color.BLACK
        }

        "returns the background colour when the ray misses" {
            val tracer = PathTrace(world(doesHit = false, stop = false, background = Color.BLUE))

            val color = tracer.trace(ray, depth = 1)

            color shouldBeApprox Color.BLUE
        }

        "returns the hit material's path shade on a deeper bounce" {
            val red = constantMaterial(Color.RED)
            val tracer = PathTrace(world(doesHit = true, stop = false, material = red))

            val color = tracer.trace(ray, depth = 1)

            color shouldBeApprox Color.RED
        }

        "averages primary-ray paths to the same value when every path is identical" {
            // Every path returns RED, so the depth-0 average over many samples is exactly RED.
            val red = constantMaterial(Color.RED)
            val tracer = PathTrace(world(doesHit = true, stop = false, material = red), numSamples = 16)

            val color = tracer.trace(ray, depth = 0)

            color shouldBeApprox Color.RED
        }
    })
