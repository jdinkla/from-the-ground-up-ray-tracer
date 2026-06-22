package net.dinkla.raytracer.examples.textures

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.textures.Checker3D
import net.dinkla.raytracer.textures.PlaneChecker
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * AC#1: the two checker textures rendered through spatially-varying matte materials. A ground plane
 * carries a 2D [PlaneChecker] (a tiled floor with grout lines, keyed on the xz coordinates of the
 * hit point), and a sphere at the origin carries a solid 3D [Checker3D] (keyed on all three
 * coordinates, so the cubes intersect the sphere surface). Both objects are origin-centred /
 * axis-aligned so their local hit points map correctly (TASK-18.1 limitation).
 */
object ProceduralCheckers : WorldDefinition {
    override val id: String = "ProceduralCheckers.kt"

    override fun world(): World =
        Builder.build {
            camera(d = 1100.0, eye = p(0.0, 2.5, 6.0), lookAt = p(0.0, 0.0, 0.0))

            ambientLight(ls = 1.0)

            lights {
                pointLight(location = p(4, 6, 5), ls = 1.0)
            }

            val planeChecker =
                PlaneChecker(
                    size = 1.0,
                    lineWidth = 0.04,
                    color1 = c(0.85, 0.85, 0.85),
                    color2 = c(0.2, 0.2, 0.2),
                    lineColor = c(0.6, 0.2, 0.2),
                )
            val solidChecker =
                Checker3D(
                    size = 0.5,
                    color1 = c(0.9, 0.7, 0.2),
                    color2 = c(0.2, 0.3, 0.7),
                )

            materials {
                svMatte(id = "floor", texture = planeChecker, ka = 0.5, kd = 0.8)
                svMatte(id = "ball", texture = solidChecker, ka = 0.5, kd = 0.85)
            }

            objects {
                plane(material = "floor", point = p(0.0, -1.0, 0.0), normal = Normal.UP)
                sphere(material = "ball", center = p(0.0, 0.0, 0.0), radius = 1.0)
            }
        }
}
