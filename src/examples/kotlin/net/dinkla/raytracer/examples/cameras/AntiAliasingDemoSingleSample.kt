package net.dinkla.raytracer.examples.cameras

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.world.Builder.build
import net.dinkla.raytracer.world.WorldDefinition

/**
 * The aliased control for [AntiAliasingDemo] (TASK-30): identical geometry and camera, but no
 * `samples(...)` call, so it renders through the single-ray path at one sample per pixel and shows
 * the stair-stepped sphere silhouette the multi-sample demo smooths.
 */
object AntiAliasingDemoSingleSample : WorldDefinition {
    override val id: String = "AntiAliasingDemoSingleSample.kt"

    override fun world() =
        build {
            camera(d = 1500.0, eye = p(0.0, 1.0, 6.0), lookAt = p(0.0, 1.0, 0.0))

            ambientLight(color = Color.WHITE, ls = 0.25)

            lights {
                pointLight(location = Point3D(3.0, 4.0, 6.0), ls = 2.0)
            }

            materials {
                matte(id = "sphere", ka = 0.4, kd = 0.85, cd = c(1.0, 0.85, 0.1))
                matte(id = "floor", ka = 0.4, kd = 0.6, cd = c(0.12, 0.12, 0.14))
            }

            objects {
                sphere(material = "sphere", center = p(0, 1, 0), radius = 1.0)
                plane(material = "floor", point = Point3D.ORIGIN, normal = Normal.UP)
            }
        }
}
