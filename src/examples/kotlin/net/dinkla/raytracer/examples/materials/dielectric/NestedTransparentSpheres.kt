package net.dinkla.raytracer.examples.materials.dielectric

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.textures.PlaneChecker
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * AC#1 for TASK-51: **nested transparent objects** (Suffern, *Ray Tracing from the Ground Up*,
 * §28.5, Figure 28.15) — the headline ch. 28 capability that had no example. Three concentric
 * [net.dinkla.raytracer.materials.Dielectric] spheres, each a different medium, so a single primary
 * ray crosses *six* dielectric boundaries (three on the way in, three on the way out) and the inner
 * spheres are visible through the outer ones.
 *
 * The materials are nested, so each shell's `iorIn`/`iorOut` describe the index of refraction on its
 * two sides relative to the medium it sits inside, exactly as the book does it:
 *
 *  - **Outer — blue glass:** `iorIn = 1.5` (glass) / `iorOut = 1.0` (air) — the glass↔air boundary.
 *  - **Middle — lemon diamond:** `iorIn = 2.42` (diamond) / `iorOut = 1.5` (glass) — the
 *    diamond↔glass boundary; diamond's high index bends rays sharply.
 *  - **Inner — mauve water:** `iorIn = 1.33` (water) / `iorOut = 2.42` (diamond) — the
 *    water↔diamond boundary (relative eta `1.33/2.42 < 1`, the optically-rarer side, so grazing rays
 *    inside the diamond reflect back).
 *
 * Each medium also has its own Beer's-law inside filter colour (`cfIn`): blue, lemon-yellow and mauve
 * respectively, so the path through each shell tints the ray and the layered colours read as
 * concentric coloured regions.
 *
 * **Recursion depth.** Six dielectric boundaries plus the Fresnel reflection each one spawns is deep
 * recursion; the book renders Fig 28.15 at a high `max_depth`, and so does this scene. A primary ray
 * that passes straight through all three shells crosses six boundaries, so at the default recursion
 * limit of `5` it would be truncated to the background on the way out and the innermost sphere would
 * read black. The scene therefore raises the limit to `12` with [maxDepth] so the transmitted rays
 * reach the floor and the mauve inner sphere resolves — the inner shells genuinely show through.
 *
 * Uses the Whitted tracer (recursive reflection/refraction) — dielectrics need it. Background is a
 * white room (floor + far wall) so transmitted rays that exit the spheres land on a bright surface,
 * which is what makes the layered colours legible rather than reading against black. This scene lives
 * in the coverage-excluded examples zone and is verified by rendering, not by unit tests.
 */
object NestedTransparentSpheres : WorldDefinition {
    override val id: String = "NestedTransparentSpheres.kt"

    private const val OUTER_RADIUS = 2.0
    private const val MIDDLE_RADIUS = 1.4
    private const val INNER_RADIUS = 0.8

    override fun world(): World =
        Builder.build {
            metadata {
                id(id)
                title("Nested transparent spheres (blue glass / lemon diamond / mauve water)")
                description(
                    "Three concentric Dielectric spheres (Suffern Fig 28.15): glass-air, diamond-glass " +
                        "and water-diamond boundaries with per-medium Beer's-law filter colours. The inner " +
                        "spheres are visible through the outer ones; rendered with the Whitted tracer.",
                )
                preferredTracer(Tracers.WHITTED)
            }

            // Six dielectric boundaries (three in, three out) plus Fresnel reflections need more than
            // the default depth of 5, or the straight-through ray is truncated and the inner sphere
            // reads black. Raise the limit so the inner shells resolve (Suffern's high max_depth).
            maxDepth(12)

            // Pulled back and slightly above so the concentric shells fill the frame against the room.
            camera(d = 1500.0, eye = p(0.0, 2.5, 9.0), lookAt = p(0.0, 0.0, 0.0))

            ambientLight(ls = 0.6)

            lights {
                pointLight(location = p(6, 9, 7), ls = 1.0)
                pointLight(location = p(-7, 6, 5), ls = 0.7)
            }

            // A white checker floor so the floor reads as white overall but still gives the eye a
            // reference grid refracted through the spheres.
            val floorChecker =
                PlaneChecker(
                    size = 1.0,
                    lineWidth = 0.0,
                    color1 = c(1.0, 1.0, 1.0),
                    color2 = c(0.78, 0.80, 0.86),
                    lineColor = c(0.78, 0.80, 0.86),
                )

            materials {
                svMatte(id = "floor", texture = floorChecker, ka = 0.6, kd = 0.85)
                // Bright white far wall: the background transmitted rays land on (non-black backdrop).
                matte(id = "wall", cd = c(0.96, 0.96, 0.98), ka = 0.8, kd = 0.7)

                // Outer shell: blue glass, glass-air boundary.
                dielectric(
                    id = "blueGlass",
                    iorIn = 1.5,
                    iorOut = 1.0,
                    cfIn = c(0.65, 0.80, 1.0),
                    cfOut = c(1.0, 1.0, 1.0),
                    ka = 0.1,
                    kd = 0.1,
                    ks = 0.4,
                    cs = c(1.0, 1.0, 1.0),
                    exp = 4000.0,
                )

                // Middle shell: lemon diamond, diamond-glass boundary (iorOut = the surrounding glass).
                dielectric(
                    id = "lemonDiamond",
                    iorIn = 2.42,
                    iorOut = 1.5,
                    cfIn = c(1.0, 0.97, 0.55),
                    cfOut = c(1.0, 1.0, 1.0),
                    ka = 0.1,
                    kd = 0.1,
                    ks = 0.4,
                    cs = c(1.0, 1.0, 1.0),
                    exp = 4000.0,
                )

                // Inner shell: mauve water, water-diamond boundary (iorOut = the surrounding diamond).
                dielectric(
                    id = "mauveWater",
                    iorIn = 1.33,
                    iorOut = 2.42,
                    cfIn = c(0.86, 0.66, 0.92),
                    cfOut = c(1.0, 1.0, 1.0),
                    ka = 0.1,
                    kd = 0.1,
                    ks = 0.4,
                    cs = c(1.0, 1.0, 1.0),
                    exp = 4000.0,
                )
            }

            objects {
                plane(material = "floor", point = p(0.0, -2.0, 0.0), normal = Normal.UP)
                plane(material = "wall", point = p(0.0, 0.0, -7.0), normal = n(0, 0, 1))

                // The three concentric shells, largest first.
                sphere(material = "blueGlass", center = p(0.0, 0.0, 0.0), radius = OUTER_RADIUS)
                sphere(material = "lemonDiamond", center = p(0.0, 0.0, 0.0), radius = MIDDLE_RADIUS)
                sphere(material = "mauveWater", center = p(0.0, 0.0, 0.0), radius = INNER_RADIUS)
            }
        }
}
