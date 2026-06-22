package net.dinkla.raytracer.examples.textures

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.textures.Ramp
import net.dinkla.raytracer.textures.Wireframe
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * AC#2 demo: the [Ramp] and [Wireframe] procedural textures, each rendered through a
 * spatially-varying matte. The left sphere carries a vertical colour [Ramp] (a gradient banded along
 * the y axis of the local hit point); the right sphere carries a [Wireframe] (a grid of wires over a
 * fill colour, keyed on the xz coordinates). Both are origin-relative so their local hit points map
 * correctly (TASK-18.1 limitation). A neutral floor grounds the scene.
 */
object RampAndWireframe : WorldDefinition {
    override val id: String = "RampAndWireframe.kt"

    override fun world(): World =
        Builder.build {
            camera(d = 1100.0, eye = p(0.0, 2.0, 7.0), lookAt = p(0.0, 0.0, 0.0))

            ambientLight(ls = 1.0)

            lights {
                pointLight(location = p(4, 6, 5), ls = 1.0)
            }

            val ramp =
                Ramp(
                    color1 = c(0.1, 0.1, 0.6),
                    color2 = c(0.95, 0.6, 0.1),
                    axis = Ramp.Axis.Y,
                    frequency = 0.5,
                )
            val wireframe =
                Wireframe(
                    size = 0.4,
                    wireWidth = 0.03,
                    fillColor = c(0.85, 0.85, 0.85),
                    wireColor = c(0.1, 0.1, 0.1),
                )

            materials {
                svMatte(id = "rampBall", texture = ramp, ka = 0.5, kd = 0.85)
                svMatte(id = "wireBall", texture = wireframe, ka = 0.5, kd = 0.85)
                matte(id = "floor", cd = c(0.6, 0.6, 0.6), ka = 0.4, kd = 0.6)
            }

            objects {
                plane(material = "floor", point = p(0.0, -1.2, 0.0), normal = Normal.UP)
                sphere(material = "rampBall", center = p(-1.4, 0.0, 0.0), radius = 1.0)
                sphere(material = "wireBall", center = p(1.4, 0.0, 0.0), radius = 1.0)
            }
        }
}
