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
import net.dinkla.raytracer.math.WrappedDouble
import net.dinkla.raytracer.objects.IGeometricObject
import net.dinkla.raytracer.shouldBeApprox
import net.dinkla.raytracer.world.IWorld

/**
 * Tests the [Whitted] tracer's branch contract: black past the recursion bound, the background colour
 * on a miss, the material's `shade` on a hit, and red when a hit object carries no material. A fake
 * [IWorld] drives the hit/miss/stop decisions and a fake material returns a fixed colour, so the
 * tracer's plumbing is observable without a full scene.
 */
internal class WhittedTest :
    StringSpec({

        // A material whose shade returns a fixed colour, so the tracer's hit path is observable.
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

                override fun getLe(sr: IShade): Color = color
            }

        // Minimal geometric object: only carries a material; the tracer reads it via Shade.material.
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
            hitDistance: Double = 2.0,
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
                        sr.t = hitDistance
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
            val tracer = Whitted(world(doesHit = true, stop = true))

            val color = tracer.trace(ray, depth = 1)

            color shouldBeApprox Color.BLACK
        }

        "returns the background colour when the ray misses" {
            val tracer = Whitted(world(doesHit = false, stop = false, background = Color.BLUE))

            val color = tracer.trace(ray, depth = 0)

            color shouldBeApprox Color.BLUE
        }

        "returns the hit material's shade colour on a hit" {
            val tracer = Whitted(world(doesHit = true, stop = false, material = constantMaterial(Color.GREEN)))

            val color = tracer.trace(ray, depth = 0)

            color shouldBeApprox Color.GREEN
        }

        "returns red when the hit object carries no material" {
            val tracer = Whitted(world(doesHit = true, stop = false, material = null))

            val color = tracer.trace(ray, depth = 0)

            color shouldBeApprox Color.RED
        }

        "reports the nearest hit distance through the tmin out-parameter on a hit" {
            val tracer =
                Whitted(
                    world(doesHit = true, stop = false, material = constantMaterial(Color.GREEN), hitDistance = 3.5),
                )
            val tmin = WrappedDouble.createMax()

            tracer.trace(ray, tmin, depth = 0)

            tmin.value shouldBeApprox 3.5
        }
    })
