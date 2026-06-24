package net.dinkla.raytracer.examples.materials.dielectric

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.textures.PlaneChecker
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * AC#3 for TASK-51: a **transparent glass box / block** (Suffern, *Ray Tracing from the Ground Up*,
 * §28.5, Figures 28.20 and 28.22). A single [net.dinkla.raytracer.materials.Dielectric]
 * [net.dinkla.raytracer.objects.AlignedBox] at glass index (`iorIn = 1.5`, `iorOut = 1.0`).
 *
 * The optical point of a *box* (vs. a sphere) is total internal reflection on its flat faces. Because
 * the relative index `eta = 1.5` is above `sqrt(2) ≈ 1.414`, the critical angle is below 45°, so a
 * ray that enters the front face and reaches a *side* face (which meets the front at 90°, i.e. an
 * internal angle of incidence near 45°) exceeds the critical angle and is **totally internally
 * reflected**: the faces adjacent to the face you look through act as **mirrors**. This is exactly
 * Suffern's observation for Fig 28.20 — `eta > sqrt(2)` makes the neighbouring faces mirror-like.
 *
 * Two further ch. 28 effects are visible:
 *  - **Path-length colour deepening (Beer's law):** the box's faint green inside filter (`cfIn`)
 *    deepens with how far a ray travels inside the glass, so the longer diagonal paths read greener
 *    than the short straight-through paths (Fig 28.22).
 *  - **Refraction of the scene behind:** the coloured spheres set behind and beside the box appear
 *    bent and mirrored in the block.
 *
 * Coloured spheres and a checker floor surround the box so the mirror faces have something to
 * reflect; the far wall is a non-black backdrop. Uses the Whitted tracer (recursive
 * reflection/refraction). This scene lives in the coverage-excluded examples zone and is verified by
 * rendering, not by unit tests.
 */
object TransparentGlassBox : WorldDefinition {
    override val id: String = "TransparentGlassBox.kt"

    override fun world(): World =
        Builder.build {
            metadata {
                id(id)
                title("Transparent glass box (TIR mirror faces, Beer-Lambert path-length tint)")
                description(
                    "A Dielectric AlignedBox at eta=1.5 (> sqrt(2)) so faces adjacent to the viewed face " +
                        "act as mirrors via total internal reflection (Suffern Figs 28.20/28.22); the inside " +
                        "filter deepens with path length. Rendered with the Whitted tracer.",
                )
                preferredTracer(Tracers.WHITTED)
            }

            // Three-quarter view so the front face and at least one side face are both visible — the
            // side face is where the TIR mirror effect shows.
            camera(d = 1500.0, eye = p(4.2, 3.0, 6.5), lookAt = p(0.0, 0.4, 0.0))

            ambientLight(ls = 0.6)

            lights {
                pointLight(location = p(7, 10, 8), ls = 1.0)
                pointLight(location = p(-6, 7, 6), ls = 0.7)
            }

            val floorChecker =
                PlaneChecker(
                    size = 1.0,
                    lineWidth = 0.0,
                    color1 = c(0.92, 0.92, 0.92),
                    color2 = c(0.20, 0.30, 0.55),
                    lineColor = c(0.20, 0.30, 0.55),
                )

            materials {
                svMatte(id = "floor", texture = floorChecker, ka = 0.5, kd = 0.85)
                // Warm non-black backdrop wall.
                matte(id = "wall", cd = c(0.85, 0.80, 0.70), ka = 0.7, kd = 0.6)
                phong(id = "red", cd = c(0.85, 0.2, 0.2), ka = 0.3, kd = 0.7, ks = 0.5, exp = 60.0)
                phong(id = "green", cd = c(0.2, 0.75, 0.3), ka = 0.3, kd = 0.7, ks = 0.5, exp = 60.0)
                phong(id = "blue", cd = c(0.25, 0.4, 0.9), ka = 0.3, kd = 0.7, ks = 0.5, exp = 60.0)

                // Green-tinted glass block: eta = 1.5 > sqrt(2) -> adjacent faces mirror via TIR.
                dielectric(
                    id = "glassBlock",
                    iorIn = 1.5,
                    iorOut = 1.0,
                    // Faint green inside filter: deepens with the path length through the block.
                    cfIn = c(0.75, 0.95, 0.80),
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
                plane(material = "wall", point = p(0.0, 0.0, -6.0), normal = n(0, 0, 1))

                // Coloured spheres around the block for the mirror faces to reflect and for the block to
                // refract: behind, to the side, and one peeking over the top.
                sphere(material = "red", center = p(-3.0, 0.0, -3.0), radius = 1.0)
                sphere(material = "green", center = p(3.5, 0.0, -2.0), radius = 1.0)
                sphere(material = "blue", center = p(0.0, 0.0, -4.0), radius = 1.0)

                // The glass block: a tall-ish box standing on the floor, centred on the origin.
                alignedBox(material = "glassBlock", p = p(-1.4, -1.0, -1.4), q = p(1.4, 2.2, 1.4))
            }
        }
}
