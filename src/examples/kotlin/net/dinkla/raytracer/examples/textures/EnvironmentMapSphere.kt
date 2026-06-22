package net.dinkla.raytracer.examples.textures

import net.dinkla.raytracer.mappings.SphericalMap
import net.dinkla.raytracer.textures.ImageReader
import net.dinkla.raytracer.textures.ImageTexture
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * AC#4 (environment map, textured-object route): a large enclosing sphere centred at the origin,
 * given a self-luminous [net.dinkla.raytracer.materials.SvEmissive] material backed by an image
 * texture + [SphericalMap], acts as a spherical environment map. Camera rays that miss the
 * foreground objects hit the surrounding sphere and read the environment image directly. The
 * [SphericalMap] normalises the hit point, so the large radius is fine.
 *
 * (The alternative route — `lights { environmentLight(material = ...) }` plus an AREA tracer — is
 * also wired in the DSL; this scene uses the simpler, directly-viewable textured-object route.)
 */
object EnvironmentMapSphere : WorldDefinition {
    override val id: String = "EnvironmentMapSphere.kt"

    override fun world(): World =
        Builder.build {
            camera(d = 1000.0, eye = p(0.0, 0.0, 6.0), lookAt = p(0.0, 0.0, 0.0))

            ambientLight(ls = 1.0)

            lights {
                pointLight(location = p(3, 4, 5), ls = 1.0)
            }

            val envTexture = ImageTexture(ImageReader.fromFile("resources/texture-test.png"), SphericalMap())

            materials {
                svEmissive(id = "environment", texture = envTexture, ls = 1.0)
                phong(id = "ball", cd = c(0.4, 0.5, 0.9), ka = 0.4, kd = 0.7, ks = 0.6, exp = 20.0)
            }

            objects {
                // Large enclosing sphere = the environment map.
                sphere(material = "environment", center = p(0.0, 0.0, 0.0), radius = 100.0)
                // A foreground ball to show the environment is behind/around it.
                sphere(material = "ball", center = p(0.0, 0.0, 0.0), radius = 1.5)
            }
        }
}
