package net.dinkla.raytracer.examples.materials.transparent

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.textures.PlaneChecker
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * TASK-50 AC#2: a reflective sphere and a transparent (glass) sphere over a checker plane, at the
 * proper glass index of refraction `ior = 1.5` — Suffern Figs 27.12/27.13, §27.7.2.
 *
 * This reproduces the book's canonical transparency test scene. The glass sphere uses the exact
 * parameters from Listing 27.5: `ks = 0.5`, `exp = 2000`, `ior = 1.5`, `kr = 0.1`, `kt = 0.9`. At this
 * crown-glass index (Table 27.1: 1.52) the checker floor is bent and inverted through the sphere
 * (refraction), and the rim totally internally reflects. Alongside it sits a perfect-mirror reflective
 * sphere, so the two specular behaviours — mirror reflection vs. refraction-plus-reflection — can be
 * compared side by side. Suffern renders this with `max_depth` from 2 to 5; the world's default
 * maximal recursion depth of 5 matches Fig 27.13(c) and is enough for the transmitted rays to leave
 * the sphere.
 *
 * The background is a non-black grey-teal backdrop plane (the book's hint for transparent objects).
 * Rendered with the Whitted tracer. This scene lives in the coverage-excluded examples zone and is
 * verified by rendering, not by unit tests.
 */
object ReflectiveAndTransparentSpheres : WorldDefinition {
    override val id: String = "ReflectiveAndTransparentSpheres.kt"

    // Crown-glass index of refraction (Suffern Listing 27.5 / Table 27.1 ≈ 1.52).
    private const val GLASS_IOR = 1.5

    override fun world(): World =
        Builder.build {
            metadata {
                id(id)
                title("Reflective and transparent spheres over a checker plane")
                description(
                    "A perfect-mirror reflective sphere and a glass transparent sphere (ior = 1.5, " +
                        "ks = 0.5, exp = 2000, kr = 0.1, kt = 0.9) over a checker plane — Suffern " +
                        "Figs 27.12/27.13. The checker is refracted and inverted through the glass.",
                )
                preferredTracer(Tracers.WHITTED)
            }

            camera(d = 1500.0, eye = p(0.0, 4.0, 11.0), lookAt = p(0.0, 0.0, 0.0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(8, 12, 9), ls = 1.0)
                pointLight(location = p(-9, 7, 6), ls = 0.6)
            }

            val floorChecker =
                PlaneChecker(
                    size = 1.5,
                    lineWidth = 0.0,
                    color1 = c(0.92, 0.92, 0.92),
                    color2 = c(0.35, 0.35, 0.35),
                    lineColor = c(0.35, 0.35, 0.35),
                )

            materials {
                svMatte(id = "floor", texture = floorChecker, ka = 0.5, kd = 0.85)
                matte(id = "backdrop", cd = c(0.18, 0.40, 0.42), ka = 0.8, kd = 0.7)
                reflective(
                    id = "mirror",
                    cd = c(0.2, 0.3, 0.8),
                    ka = 0.25,
                    kd = 0.5,
                    ks = 0.5,
                    exp = 2000.0,
                    kr = 0.85,
                    cr = c(0.9, 0.9, 1.0),
                )
                // Glass sphere: exact Suffern Listing 27.5 parameters.
                transparent(
                    id = "glass",
                    cd = Color.WHITE,
                    ka = 0.0,
                    kd = 0.0,
                    ks = 0.5,
                    exp = 2000.0,
                    ior = GLASS_IOR,
                    kr = 0.1,
                    kt = 0.9,
                )
            }

            objects {
                plane(material = "floor", point = p(0.0, -1.0, 0.0), normal = Normal.UP)
                plane(material = "backdrop", point = p(0.0, 0.0, -8.0), normal = n(0, 0, 1))
                sphere(material = "mirror", center = p(-2.3, 0.0, 0.0), radius = 1.0)
                sphere(material = "glass", center = p(2.0, 0.0, 0.5), radius = 1.3)
            }
        }
}
