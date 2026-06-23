package net.dinkla.raytracer.tracers

import io.kotest.core.spec.style.StringSpec
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.hits.IHit
import net.dinkla.raytracer.hits.IShade
import net.dinkla.raytracer.lights.Ambient
import net.dinkla.raytracer.lights.Light
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Ray
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.math.WrappedDouble
import net.dinkla.raytracer.shouldBeApprox
import net.dinkla.raytracer.world.IWorld

/**
 * Tests the [SingleSphere] tracer: it paints red on any hit and the background colour on a miss, and
 * its `tmin` overload always returns the background colour.
 */
internal class SingleSphereTest :
    StringSpec({

        fun world(
            doesHit: Boolean,
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
                ): Boolean = doesHit

                override fun inShadow(
                    ray: Ray,
                    sr: IShade,
                    d: Double,
                ): Boolean = false

                override fun shouldStopRecursion(depth: Int): Boolean = false
            }

        val ray = Ray(Point3D(0.0, 0.0, 1.0), Vector3D(0.0, 0.0, -1.0))

        "paints red on a hit" {
            val tracer = SingleSphere(world(doesHit = true))

            val color = tracer.trace(ray, depth = 0)

            color shouldBeApprox Color.RED
        }

        "returns the background colour on a miss" {
            val tracer = SingleSphere(world(doesHit = false, background = Color.BLUE))

            val color = tracer.trace(ray, depth = 0)

            color shouldBeApprox Color.BLUE
        }

        "the tmin overload always returns the background colour" {
            val tracer = SingleSphere(world(doesHit = true, background = Color.GREEN))

            val color = tracer.trace(ray, WrappedDouble.createMax(), depth = 0)

            color shouldBeApprox Color.GREEN
        }
    })
