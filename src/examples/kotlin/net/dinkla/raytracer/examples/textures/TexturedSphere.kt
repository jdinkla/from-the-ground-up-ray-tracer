package net.dinkla.raytracer.examples.textures

import net.dinkla.raytracer.mappings.SphericalMap
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.textures.ImageReader
import net.dinkla.raytracer.textures.ImageTexture
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * AC#3 (sphere): a unit sphere at the origin textured with an image via [SphericalMap]. The map uses
 * the (local) hit point as a direction on the unit sphere, so the sphere must be the unit sphere
 * centred at the origin for the mapping to be correct (Instance-local UVs are out of scope here).
 * The four-quadrant grid texture makes the mapping orientation easy to verify by eye.
 */
object TexturedSphere : WorldDefinition {
    override val id: String = "TexturedSphere.kt"

    override fun world(): World =
        Builder.build {
            camera(d = 1200.0, eye = p(0.0, 0.0, 4.0), lookAt = p(0.0, 0.0, 0.0))

            ambientLight(ls = 1.0)

            lights {
                pointLight(location = p(2, 3, 4), ls = 1.0)
            }

            val texture = ImageTexture(ImageReader.fromFile("resources/texture-test.png"), SphericalMap())

            materials {
                svMatte(id = "earth", texture = texture, ka = 0.6, kd = 0.9)
                matte(id = "floor", cd = c(0.7, 0.7, 0.7), ka = 0.4, kd = 0.6)
            }

            objects {
                plane(material = "floor", point = p(0.0, -1.2, 0.0), normal = Normal.UP)
                sphere(material = "earth", center = p(0.0, 0.0, 0.0), radius = 1.0)
            }
        }
}
