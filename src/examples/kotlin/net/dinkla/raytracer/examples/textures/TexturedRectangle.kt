package net.dinkla.raytracer.examples.textures

import net.dinkla.raytracer.mappings.RectangularMap
import net.dinkla.raytracer.textures.ImageReader
import net.dinkla.raytracer.textures.ImageTexture
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * AC#3 (rectangle): a 2x2 rectangle in the xz-plane spanning `[-1,1] x [-1,1]`, textured with an
 * image via [RectangularMap] (x -> column, z -> row over `[-1,1]`). The hit point falls directly in
 * the mapping's coordinate range because the rectangle is axis-aligned at the origin.
 */
object TexturedRectangle : WorldDefinition {
    override val id: String = "TexturedRectangle.kt"

    override fun world(): World =
        Builder.build {
            camera(d = 1400.0, eye = p(0.0, 3.0, 3.0), lookAt = p(0.0, 0.0, 0.0))

            ambientLight(ls = 1.0)

            lights {
                pointLight(location = p(2, 5, 3), ls = 1.0)
            }

            val texture =
                ImageTexture(
                    ImageReader.fromFile("resources/texture-test.png"),
                    RectangularMap(uExtent = 1.0, vExtent = 1.0),
                )

            materials {
                svMatte(id = "tex", texture = texture, ka = 0.6, kd = 0.9)
            }

            objects {
                // Corner at (-1,0,-1), spanning +2 in x and +2 in z -> the rectangle [-1,1]x[-1,1].
                rectangle(material = "tex", p0 = p(-1.0, 0.0, -1.0), a = v(2.0, 0.0, 0.0), b = v(0.0, 0.0, 2.0))
            }
        }
}
