package net.dinkla.raytracer.examples.materials.dielectric

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.textures.PlaneChecker
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * AC#1 for TASK-19: a physically based glass [net.dinkla.raytracer.materials.Dielectric] sphere over
 * a checkered floor. The render should show all three Dielectric effects:
 *
 *  - **Refraction:** the checker pattern is bent and inverted when seen *through* the sphere
 *    (Snell's law, index of refraction 1.5).
 *  - **Total internal reflection:** near the silhouette the grazing rays cannot exit the glass, so
 *    the rim reflects the surroundings/floor rather than transmitting them.
 *  - **Colored attenuation (Beer's law):** the inside filter colour [cfIn] tints rays by their path
 *    length through the glass, so the sphere reads faintly green/teal where the path is longest.
 *
 * Sphere is origin-centred so the (untextured) Dielectric needs no local-hit-point mapping. Rendered
 * with the Whitted tracer (recursive reflection/refraction). This scene lives in the coverage-excluded
 * examples zone and is verified by rendering, not by unit tests.
 */
object GlassSphere : WorldDefinition {
    override val id: String = "GlassSphere.kt"

    override fun world(): World =
        Builder.build {
            camera(d = 1400.0, eye = p(0.0, 3.0, 7.0), lookAt = p(0.0, 0.0, 0.0))

            ambientLight(ls = 0.6)

            lights {
                pointLight(location = p(5, 8, 6), ls = 1.0)
                pointLight(location = p(-6, 5, 4), ls = 0.6)
            }

            val floorChecker =
                PlaneChecker(
                    size = 1.0,
                    lineWidth = 0.0,
                    color1 = c(0.95, 0.95, 0.95),
                    color2 = c(0.15, 0.25, 0.55),
                    lineColor = c(0.15, 0.25, 0.55),
                )

            materials {
                svMatte(id = "floor", texture = floorChecker, ka = 0.5, kd = 0.85)
                matte(id = "back", cd = c(0.7, 0.75, 0.85), ka = 0.7, kd = 0.6)
                dielectric(
                    id = "glass",
                    iorIn = 1.5,
                    iorOut = 1.0,
                    // Faint green/teal tint that deepens with path length through the glass (Beer's law).
                    cfIn = c(0.65, 0.95, 0.85),
                    cfOut = c(1.0, 1.0, 1.0),
                    ka = 0.1,
                    kd = 0.1,
                    ks = 0.4,
                    cs = c(1.0, 1.0, 1.0),
                    exp = 4000.0,
                )
            }

            objects {
                plane(material = "floor", point = p(0.0, -1.0, 0.0), normal = Normal.UP)
                plane(material = "back", point = p(0.0, 0.0, -6.0), normal = n(0, 0, 1))
                sphere(material = "glass", center = p(0.0, 0.0, 0.0), radius = 1.0)
            }
        }
}
