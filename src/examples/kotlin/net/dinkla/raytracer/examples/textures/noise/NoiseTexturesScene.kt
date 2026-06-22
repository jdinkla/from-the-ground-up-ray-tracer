package net.dinkla.raytracer.examples.textures.noise

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.noise.CubicNoise
import net.dinkla.raytracer.textures.FBmTexture
import net.dinkla.raytracer.textures.PlaneChecker
import net.dinkla.raytracer.textures.TurbulenceTexture
import net.dinkla.raytracer.textures.Wood
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * TASK-18.3 AC#2 (fBm, turbulence, wood): three origin-relative spheres side by side, each carrying a
 * different noise texture through a spatially-varying matte:
 *
 *  - left: [FBmTexture] — soft, cloud-like fractal variation between two blues;
 *  - centre: [TurbulenceTexture] — billowy, smoky veins (sum of `|noise|` octaves);
 *  - right: [Wood] — concentric growth rings around the y axis, warped by turbulence.
 *
 * All three share a single [CubicNoise]. The spheres straddle the origin slightly so the noise pattern
 * differs visibly between them while their local hit points still map correctly (TASK-18.1 limitation).
 */
object NoiseTexturesScene : WorldDefinition {
    override val id: String = "NoiseTexturesScene.kt"

    override fun world(): World =
        Builder.build {
            camera(d = 900.0, eye = p(0.0, 2.0, 8.0), lookAt = p(0.0, 0.0, 0.0))

            ambientLight(ls = 1.0)

            lights {
                pointLight(location = p(4, 6, 5), ls = 1.0)
            }

            val noise = CubicNoise(numOctaves = 6)

            val fbm =
                FBmTexture(
                    noise = noise,
                    minColor = c(0.1, 0.2, 0.5),
                    maxColor = c(0.7, 0.85, 1.0),
                )
            val turbulence =
                TurbulenceTexture(
                    noise = noise,
                    minColor = c(0.95, 0.95, 0.95),
                    maxColor = c(0.15, 0.1, 0.25),
                )
            val wood =
                Wood(
                    noise = noise,
                    lightColor = c(0.66, 0.45, 0.24),
                    darkColor = c(0.30, 0.16, 0.06),
                    ringFrequency = 4.0,
                    ringWarp = 1.2,
                )
            val floorChecker =
                PlaneChecker(
                    size = 1.0,
                    color1 = c(0.8, 0.8, 0.8),
                    color2 = c(0.4, 0.4, 0.4),
                )

            materials {
                svMatte(id = "fbm", texture = fbm, ka = 0.5, kd = 0.85)
                svMatte(id = "turb", texture = turbulence, ka = 0.5, kd = 0.85)
                svMatte(id = "wood", texture = wood, ka = 0.5, kd = 0.85)
                svMatte(id = "floor", texture = floorChecker, ka = 0.5, kd = 0.8)
            }

            objects {
                plane(material = "floor", point = p(0.0, -1.2, 0.0), normal = Normal.UP)
                sphere(material = "fbm", center = p(-2.4, 0.0, 0.0), radius = 1.0)
                sphere(material = "turb", center = p(0.0, 0.0, 0.0), radius = 1.0)
                sphere(material = "wood", center = p(2.4, 0.0, 0.0), radius = 1.0)
            }
        }
}
