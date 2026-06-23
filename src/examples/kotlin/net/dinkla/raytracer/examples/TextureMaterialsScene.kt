package net.dinkla.raytracer.examples

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.noise.CubicNoise
import net.dinkla.raytracer.textures.ConstantColor
import net.dinkla.raytracer.textures.WrappedFBmTexture
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition

/**
 * Coverage example (TASK-42) for the [net.dinkla.raytracer.materials.SvPhong] material and the
 * [ConstantColor] and [WrappedFBmTexture] textures:
 *
 *  - left: a [WrappedFBmTexture] (Suffern ch. 31 marble) driven through an `svPhong` material, so the
 *    fractal colour band carries a specular highlight as well as diffuse shading;
 *  - right: a flat [ConstantColor] texture through an `svMatte`, the trivial texture that lets a
 *    spatially-varying material stand in for a plain coloured one.
 */
object TextureMaterialsScene : WorldDefinition {
    override val id: String = "TextureMaterialsScene.kt"

    override fun world() =
        Builder.build {
            camera(d = 900.0, eye = p(0.0, 2.0, 8.0), lookAt = p(0.0, 0.0, 0.0))

            ambientLight(ls = 1.0)

            lights {
                pointLight(location = p(4, 6, 5), ls = 1.0)
                pointLight(location = p(-4, 4, 3), ls = 0.6, color = c(1.0, 0.95, 0.85))
            }

            val noise = CubicNoise(numOctaves = 6)
            val marble =
                WrappedFBmTexture(
                    noise = noise,
                    minColor = c(0.1, 0.05, 0.15),
                    maxColor = c(0.95, 0.9, 1.0),
                    expansionNumber = 4.0,
                )
            val solid = ConstantColor(c(0.85, 0.4, 0.2))

            materials {
                svPhong(id = "marble", texture = marble, ka = 0.35, kd = 0.75, ks = 0.4, exp = 40.0)
                svMatte(id = "solid", texture = solid, ka = 0.35, kd = 0.8)
                matte(id = "floor", cd = c(0.55, 0.55, 0.6), ka = 0.4, kd = 0.7)
            }

            objects {
                plane(material = "floor", point = p(0.0, -1.2, 0.0), normal = Normal.UP)
                sphere(material = "marble", center = p(-1.4, 0.0, 0.0), radius = 1.2)
                sphere(material = "solid", center = p(1.4, 0.0, 0.0), radius = 1.2)
            }
        }
}
