package net.dinkla.raytracer.examples.cameras

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.world.Builder.build
import net.dinkla.raytracer.world.WorldDefinition

/**
 * Anti-aliasing demo (TASK-30). A single bright sphere on a dark plane gives a long, near-horizontal
 * silhouette whose edge aliases badly at one sample per pixel. `samples(16)` opts the scene into the
 * sampled render path so the edge is averaged from 16 jittered sub-pixel rays and renders smooth.
 *
 * Compare against a single-sample render of the same geometry (set `samples` to 1, or render
 * [YellowAndRedSphere][net.dinkla.raytracer.examples.YellowAndRedSphere]) to see the stair-stepping
 * the multi-sample path removes.
 */
object AntiAliasingDemo : WorldDefinition {
    override val id: String = "AntiAliasingDemo.kt"

    override fun world() =
        build {
            camera(d = 1500.0, eye = p(0.0, 1.0, 6.0), lookAt = p(0.0, 1.0, 0.0))

            samples(16)

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
