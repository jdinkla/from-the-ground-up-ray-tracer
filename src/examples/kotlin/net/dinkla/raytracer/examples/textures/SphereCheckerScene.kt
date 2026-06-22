package net.dinkla.raytracer.examples.textures

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.textures.PlaneChecker
import net.dinkla.raytracer.textures.SphereChecker
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * AC#1 (2D sphere checker): a sphere at the origin carries a [SphereChecker], a 2D checkerboard laid
 * out in latitude/longitude bands over the surface (with grout lines), rendered through a
 * spatially-varying matte. The sphere must be centred at the origin for the lat/long mapping to be
 * correct (TASK-18.1 limitation). A [PlaneChecker] floor gives the scene a ground.
 */
object SphereCheckerScene : WorldDefinition {
    override val id: String = "SphereCheckerScene.kt"

    override fun world(): World =
        Builder.build {
            camera(d = 1100.0, eye = p(0.0, 2.0, 6.0), lookAt = p(0.0, 0.0, 0.0))

            ambientLight(ls = 1.0)

            lights {
                pointLight(location = p(4, 6, 5), ls = 1.0)
            }

            val sphereChecker =
                SphereChecker(
                    numHorizontalCheckers = 20,
                    numVerticalCheckers = 10,
                    lineWidth = 0.06,
                    color1 = c(0.9, 0.9, 0.9),
                    color2 = c(0.15, 0.3, 0.6),
                    lineColor = c(0.05, 0.05, 0.05),
                )
            val floorChecker =
                PlaneChecker(
                    size = 1.0,
                    color1 = c(0.8, 0.8, 0.8),
                    color2 = c(0.35, 0.35, 0.35),
                )

            materials {
                svMatte(id = "globe", texture = sphereChecker, ka = 0.5, kd = 0.85)
                svMatte(id = "floor", texture = floorChecker, ka = 0.5, kd = 0.8)
            }

            objects {
                plane(material = "floor", point = p(0.0, -1.0, 0.0), normal = Normal.UP)
                sphere(material = "globe", center = p(0.0, 0.0, 0.0), radius = 1.0)
            }
        }
}
