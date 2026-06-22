package net.dinkla.raytracer.examples.textures.noise

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.noise.CubicNoise
import net.dinkla.raytracer.textures.PlaneChecker
import net.dinkla.raytracer.textures.Ramp
import net.dinkla.raytracer.textures.RampFBmTexture
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * TASK-18.3 AC#2 (marble): an origin-centred sphere carrying a [RampFBmTexture] — a colour [Ramp]
 * indexed by a sine wave along the y axis whose phase is warped by [CubicNoise] fractal noise — through
 * a spatially-varying matte. The result is the classic marble look: pale stone veined with darker
 * bands that bend and fold where the noise perturbs them. The sphere is at the origin so its local hit
 * points drive the noise correctly (TASK-18.1 limitation). A neutral checker floor grounds the scene.
 */
object MarbleScene : WorldDefinition {
    override val id: String = "MarbleScene.kt"

    override fun world(): World =
        Builder.build {
            camera(d = 1100.0, eye = p(0.0, 2.0, 6.0), lookAt = p(0.0, 0.0, 0.0))

            ambientLight(ls = 1.0)

            lights {
                pointLight(location = p(4, 6, 5), ls = 1.0)
            }

            val marble =
                RampFBmTexture(
                    noise = CubicNoise(numOctaves = 6),
                    ramp =
                        Ramp(
                            color1 = c(0.95, 0.93, 0.88),
                            color2 = c(0.22, 0.20, 0.30),
                        ),
                    axis = Ramp.Axis.Y,
                    frequency = 6.0,
                    amplitude = 4.0,
                )
            val floorChecker =
                PlaneChecker(
                    size = 1.0,
                    color1 = c(0.8, 0.8, 0.8),
                    color2 = c(0.4, 0.4, 0.4),
                )

            materials {
                svMatte(id = "marble", texture = marble, ka = 0.5, kd = 0.85)
                svMatte(id = "floor", texture = floorChecker, ka = 0.5, kd = 0.8)
            }

            objects {
                plane(material = "floor", point = p(0.0, -1.0, 0.0), normal = Normal.UP)
                sphere(material = "marble", center = p(0.0, 0.0, 0.0), radius = 1.0)
            }
        }
}
