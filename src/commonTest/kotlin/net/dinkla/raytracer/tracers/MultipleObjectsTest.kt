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
 * Tests the [MultipleObjects] tracer: the background colour on a miss, the material's `shade` on a
 * hit, the world background when a hit object has no material, and the unsupported `tmin` overload.
 */
internal class MultipleObjectsTest :
    StringSpec({

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

                override fun shouldStopRecursion(depth: Int): Boolean = false
            }

        val ray = Ray(Point3D(0.0, 0.0, 1.0), Vector3D(0.0, 0.0, -1.0))

        "returns the background colour when the ray misses" {
            val tracer = MultipleObjects(world(doesHit = false, background = Color.BLUE))

            val color = tracer.trace(ray, depth = 0)

            color shouldBeApprox Color.BLUE
        }

        "returns the hit material's shade colour on a hit" {
            val tracer = MultipleObjects(world(doesHit = true, material = constantMaterial(Color.GREEN)))

            val color = tracer.trace(ray, depth = 0)

            color shouldBeApprox Color.GREEN
        }

        "falls back to the background colour when a hit object has no material" {
            val tracer = MultipleObjects(world(doesHit = true, material = null, background = Color.YELLOW))

            val color = tracer.trace(ray, depth = 0)

            color shouldBeApprox Color.YELLOW
        }

        "the tmin overload delegates to the two-argument trace (TASK-63)" {
            // MultipleObjects no longer overrides the tmin-reporting trace to throw; it inherits
            // Tracer's default, which ignores tmin and delegates to trace(ray, depth). The result is
            // therefore identical to the two-argument call: the hit material's shade colour.
            val tracer = MultipleObjects(world(doesHit = true, material = constantMaterial(Color.GREEN)))

            val color = tracer.trace(ray, WrappedDouble.createMax(), depth = 0)

            color shouldBeApprox Color.GREEN
        }
    })
