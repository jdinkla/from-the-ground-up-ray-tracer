package net.dinkla.raytracer.examples.materials.dielectric

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.compound.SolidCylinder
import net.dinkla.raytracer.textures.PlaneChecker
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * AC#2 for TASK-52: a **glass of water** (Suffern, *Ray Tracing from the Ground Up*, §28.7,
 * Figures 28.35–28.38) — the canonical "hard" transparency scene. The glass is modelled by
 * [net.dinkla.raytracer.objects.compound.GlassOfWater] as a compound of *boundary surfaces*, each the
 * single interface between two media, with three separate [net.dinkla.raytracer.materials.Dielectric]
 * materials:
 *
 *  - **glass↔air** (`iorIn = 1.5`, `iorOut = 1.0`): the top rim, outer wall, the inner wall above the
 *    water line and the bottom of the glass.
 *  - **water↔glass** (`iorIn = 1.33`, `iorOut = 1.5`): the inner wall below the water line and the
 *    cavity floor — the relative index is `1.33/1.5 < 1` (water is optically rarer than glass), so
 *    grazing rays inside the glass reflect at this interface.
 *  - **water↔air** (`iorIn = 1.33`, `iorOut = 1.0`): the flat water surface and the meniscus. The
 *    water surface is where **total internal reflection** is most visible — looking down through the
 *    side of the glass, rays hitting the underside of the surface beyond the critical angle reflect
 *    rather than exit, mirroring the floor of the glass.
 *
 * A **[net.dinkla.raytracer.materials.Matte] straw** leans in the glass and crosses the water line; its
 * submerged part is seen through the water↔glass and water↔air boundaries, which refract it, so the
 * straw **appears to bend (and shift sideways) at the water surface** — the everyday illusion the book
 * reproduces. The glass stands on a blue/white **checker plane** so the refracted and total-internally
 * reflected floor pattern reads clearly through the glass and the water.
 *
 * **Recursion depth.** A primary ray can cross many dielectric boundaries — into the outer glass wall,
 * across the water, out the far wall, plus the Fresnel reflection each spawns — so the scene raises the
 * tracer's recursion limit with [net.dinkla.raytracer.world.dsl.WorldScope.maxDepth] (the TASK-51 DSL
 * setter) to `12`, matching the book's high `max_depth` for Figure 28.38. At the default of `5` the
 * deepest transmitted rays would be truncated to the background and the glass would read muddy.
 *
 * Uses the Whitted tracer (recursive reflection/refraction) — dielectrics need it. This scene lives in
 * the coverage-excluded examples zone and is verified by rendering, not by unit tests.
 */
object GlassOfWaterScene : WorldDefinition {
    override val id: String = "GlassOfWaterScene.kt"

    // Straw: a thin tall cylinder leaned into the glass, crossing the water surface at y = 1.4.
    private const val STRAW_RADIUS = 0.05
    private const val STRAW_BOTTOM = 0.3
    private const val STRAW_TOP = 3.1
    private const val STRAW_TILT_DEGREES = 14.0

    override fun world(): World =
        Builder.build {
            metadata {
                id(id)
                title("Glass of water (compound dielectric boundaries, bending straw)")
                description(
                    "A glass of water modelled as a compound of dielectric boundary surfaces " +
                        "(Suffern Fig 28.38): glass-air, water-glass and water-air interfaces, each with its " +
                        "own index of refraction and filter colour, over a checker plane. A Matte straw bends " +
                        "at the water line; the water surface shows total internal reflection. Whitted tracer.",
                )
                preferredTracer(Tracers.WHITTED)
            }

            // Many stacked dielectric boundaries plus their Fresnel reflections need more than the
            // default depth of 5 (Suffern's high max_depth for Fig 28.38); 12 lets the deepest
            // transmitted rays reach the floor instead of truncating to the background.
            maxDepth(12)

            // Looking down slightly from the front so the water surface and the submerged straw are both
            // visible through the glass wall.
            camera(d = 1500.0, eye = p(0.0, 2.6, 6.5), lookAt = p(0.0, 1.0, 0.0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(6, 9, 7), ls = 1.0)
                pointLight(location = p(-7, 7, 5), ls = 0.7)
            }

            val floorChecker =
                PlaneChecker(
                    size = 0.5,
                    lineWidth = 0.0,
                    color1 = c(0.95, 0.95, 0.95),
                    color2 = c(0.18, 0.32, 0.62),
                    lineColor = c(0.18, 0.32, 0.62),
                )

            materials {
                svMatte(id = "floor", texture = floorChecker, ka = 0.55, kd = 0.85)
                // Bright far wall so transmitted rays exiting the glass land on a non-black backdrop.
                matte(id = "wall", cd = c(0.9, 0.92, 0.96), ka = 0.8, kd = 0.7)
                // The straw: opaque red, so its bend at the water line is unmistakable.
                matte(id = "straw", cd = c(0.85, 0.18, 0.16), ka = 0.35, kd = 0.8)

                // glass-air boundary: glass (1.5) against air (1.0), faint green glass tint.
                dielectric(
                    id = "glassAir",
                    iorIn = 1.5,
                    iorOut = 1.0,
                    cfIn = c(0.72, 0.95, 0.85),
                    cfOut = c(1.0, 1.0, 1.0),
                    ka = 0.1,
                    kd = 0.1,
                    ks = 0.4,
                    cs = c(1.0, 1.0, 1.0),
                    exp = 4000.0,
                )
                // water-glass boundary: water (1.33) against glass (1.5).
                dielectric(
                    id = "waterGlass",
                    iorIn = 1.33,
                    iorOut = 1.5,
                    cfIn = c(0.80, 0.93, 1.0),
                    cfOut = c(1.0, 1.0, 1.0),
                    ka = 0.1,
                    kd = 0.1,
                    ks = 0.4,
                    cs = c(1.0, 1.0, 1.0),
                    exp = 4000.0,
                )
                // water-air boundary: water (1.33) against air (1.0); the surface where TIR shows.
                dielectric(
                    id = "waterAir",
                    iorIn = 1.33,
                    iorOut = 1.0,
                    cfIn = c(0.80, 0.93, 1.0),
                    cfOut = c(1.0, 1.0, 1.0),
                    ka = 0.1,
                    kd = 0.1,
                    ks = 0.4,
                    cs = c(1.0, 1.0, 1.0),
                    exp = 4000.0,
                )
            }

            objects {
                plane(material = "floor", point = p(0.0, 0.0, 0.0), normal = Normal.UP)
                plane(material = "wall", point = p(0.0, 0.0, -6.0), normal = n(0, 0, 1))

                glassOfWater(
                    glassAir = "glassAir",
                    waterGlass = "waterGlass",
                    waterAir = "waterAir",
                )

                // The straw: a thin tall cylinder, tilted and slid sideways so it leans against the
                // inner wall and crosses the water surface (y = 1.4) — the refraction at that surface is
                // what makes it look bent.
                val straw = SolidCylinder(y0 = STRAW_BOTTOM, y1 = STRAW_TOP, radius = STRAW_RADIUS)
                instance(material = "straw", of = straw) {
                    rotate(Axis.Z, STRAW_TILT_DEGREES)
                    translate(v(0.25, 0.0, 0.0))
                }
            }
        }
}
