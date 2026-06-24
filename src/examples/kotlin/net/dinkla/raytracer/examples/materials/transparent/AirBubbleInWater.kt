package net.dinkla.raytracer.examples.materials.transparent

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.textures.PlaneChecker
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * TASK-50 AC#1: an air bubble in water — a transparent ([net.dinkla.raytracer.materials.Transparent])
 * sphere whose **relative** index of refraction is below 1 (Suffern Fig 27.15(a), §27.7.3).
 *
 * The optical point: when a sphere's medium has a *smaller* index of refraction than its surroundings
 * (here air, `ior = 0.75`, inside water of ior ≈ 1.33, so the relative `eta = 0.75 < 1`), total
 * internal reflection occurs for rays that hit the sphere from the **outside** whenever the impact
 * parameter `b > eta` (Equation 27.15). Those grazing rays cannot transmit into the bubble at all, so
 * the outer annulus of the bubble acts as a mirror/dark ring — the most striking feature of the
 * figure. The book deliberately uses the "incorrect" `kr = 0.1` here (rather than the physically
 * correct `kr = 1.0` of Fig 27.15(b)) so the total-internal-reflection ring reads as a clearly
 * **dark** band rather than a perfect mirror — that dark ring is exactly what this scene demonstrates.
 *
 * A red phong sphere sits behind the bubble so the refracting/inverting transmission through the
 * bubble's centre is visible, and the background is a non-black teal backdrop plane (the book's hint:
 * always use a non-black background for transparent objects, else trapped rays read as black). Rendered
 * with the Whitted tracer (transparency is part of Whitted ray tracing). This scene lives in the
 * coverage-excluded examples zone and is verified by rendering, not by unit tests.
 */
object AirBubbleInWater : WorldDefinition {
    override val id: String = "AirBubbleInWater.kt"

    // Relative index of refraction of an air bubble in water (Suffern Q27.6): n_air / n_water ≈ 0.75.
    private const val AIR_BUBBLE_ETA = 0.75

    override fun world(): World =
        Builder.build {
            metadata {
                id(id)
                title("Air bubble in water (transparent sphere, eta < 1)")
                description(
                    "A transparent sphere with relative eta = 0.75 (an air bubble in water): rays hitting " +
                        "it from the outside with impact parameter b > eta are totally internally reflected, " +
                        "producing the dark mirror ring of Suffern Fig 27.15(a) (kr = 0.1).",
                )
                preferredTracer(Tracers.WHITTED)
            }

            camera(d = 1400.0, eye = p(0.0, 2.0, 9.0), lookAt = p(0.0, 0.5, 0.0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(6, 9, 8), ls = 1.0)
                pointLight(location = p(-7, 6, 5), ls = 0.6)
            }

            val floorChecker =
                PlaneChecker(
                    size = 1.5,
                    lineWidth = 0.0,
                    color1 = c(0.95, 0.95, 0.95),
                    color2 = c(0.35, 0.45, 0.5),
                    lineColor = c(0.35, 0.45, 0.5),
                )

            materials {
                svMatte(id = "floor", texture = floorChecker, ka = 0.5, kd = 0.85)
                // A non-black teal backdrop, matching the book's green/teal background for these figures.
                matte(id = "backdrop", cd = c(0.10, 0.45, 0.45), ka = 0.8, kd = 0.7)
                // A red sphere behind the bubble so the inverting transmission through its centre is visible.
                phong(id = "red", cd = c(0.85, 0.15, 0.15), ka = 0.35, kd = 0.7, ks = 0.5, exp = 60.0)
                // The air bubble: eta = 0.75 (< 1) and the book's kr = 0.1, so TIR reads as a dark ring.
                transparent(
                    id = "bubble",
                    cd = Color.WHITE,
                    ka = 0.0,
                    kd = 0.0,
                    ks = 0.5,
                    exp = 2000.0,
                    ior = AIR_BUBBLE_ETA,
                    kr = 0.1,
                    kt = 0.9,
                )
            }

            objects {
                plane(material = "floor", point = p(0.0, -1.0, 0.0), normal = Normal.UP)
                plane(material = "backdrop", point = p(0.0, 0.0, -7.0), normal = n(0, 0, 1))
                sphere(material = "red", center = p(0.0, 0.4, -3.0), radius = 1.0)
                sphere(material = "bubble", center = p(0.0, 0.5, 0.0), radius = 1.6)
            }
        }
}
