package net.dinkla.raytracer.examples.materials.dielectric

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.textures.PlaneChecker
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * AC#2 for TASK-53: a **fishbowl** (Suffern, *Ray Tracing from the Ground Up*, §28.8, Figures 28.39
 * and 28.41) — a spherical glass bowl, open at the top and partly filled with water, the chapter's
 * showcase compound-dielectric object. The bowl is modelled by
 * [net.dinkla.raytracer.objects.compound.FishBowl] as a compound of *boundary surfaces*, each the single
 * interface between two media, with three separate [net.dinkla.raytracer.materials.Dielectric] materials:
 *
 *  - **glass↔air** (`iorIn = 1.5`, `iorOut = 1.0`): the outer glass sphere, the dry inner wall above the
 *    water line and the torus rim around the opening.
 *  - **water↔air** (`iorIn = 1.33`, `iorOut = 1.0`): the flat water surface. Looking down through the
 *    side of the glass, rays hitting the underside of the surface beyond the critical angle reflect
 *    rather than exit (total internal reflection).
 *  - **water↔glass** (`iorIn = 1.33`, `iorOut = 1.5`): the submerged inner wall — the relative index is
 *    `1.33/1.5 < 1` (water is optically rarer than glass), so grazing rays inside the glass reflect at
 *    this interface.
 *
 * A small [net.dinkla.raytracer.materials.Matte] **fish** (an orange sphere) sits submerged in the
 * water; seen through the water↔air and glass↔air boundaries it is refracted and tinted by the water's
 * filter colour, so it reads enlarged and displaced — the everyday fishbowl distortion the book
 * reproduces. The bowl rests on a green/white **checker plane** so the refracted floor pattern reads
 * through the glass and the water.
 *
 * **Recursion depth.** A primary ray can cross many dielectric boundaries — into the outer glass wall,
 * across the water, out the far wall, plus the Fresnel reflection each spawns — so the scene raises the
 * tracer's recursion limit with [net.dinkla.raytracer.world.dsl.WorldScope.maxDepth] (the TASK-51 DSL
 * setter) to `15`, matching the book's high `max_depth` for Figure 28.41. At the default of `5` the
 * deepest transmitted rays would be truncated to the background and the bowl would read muddy.
 *
 * Uses the Whitted tracer (recursive reflection/refraction) — dielectrics need it. This scene lives in
 * the coverage-excluded examples zone and is verified by rendering, not by unit tests.
 */
object FishBowlScene : WorldDefinition {
    override val id: String = "FishBowlScene.kt"

    // The bowl: outer radius 2.0, centred at the origin, so its base sits at y = -2.0.
    private const val OUTER_RADIUS = 2.0

    // The fish: a small opaque sphere submerged in the water (water surface is at y = 0.8).
    private const val FISH_RADIUS = 0.4

    override fun world(): World =
        Builder.build {
            metadata {
                id(id)
                title("Fishbowl (compound dielectric boundaries, submerged fish)")
                description(
                    "A spherical fishbowl modelled as a compound of dielectric boundary surfaces " +
                        "(Suffern Fig 28.41): glass-air, water-air and water-glass interfaces, each with its " +
                        "own index of refraction and filter colour, over a checker plane. A submerged Matte " +
                        "fish is refracted and tinted by the water; the water surface shows total internal " +
                        "reflection. Whitted tracer.",
                )
                preferredTracer(Tracers.WHITTED)
            }

            // Many stacked dielectric boundaries plus their Fresnel reflections need more than the
            // default depth of 5 (Suffern's high max_depth for Fig 28.41); 15 lets the deepest
            // transmitted rays reach the floor instead of truncating to the background.
            maxDepth(15)

            // Looking down slightly from the front so the open top, the water surface and the submerged
            // fish are all visible through the glass wall.
            camera(d = 1400.0, eye = p(0.0, 2.4, 8.0), lookAt = p(0.0, -0.2, 0.0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(7, 10, 8), ls = 1.0)
                pointLight(location = p(-8, 8, 6), ls = 0.7)
            }

            val floorChecker =
                PlaneChecker(
                    size = 0.6,
                    lineWidth = 0.0,
                    color1 = c(0.95, 0.95, 0.95),
                    color2 = c(0.22, 0.55, 0.35),
                    lineColor = c(0.22, 0.55, 0.35),
                )

            materials {
                svMatte(id = "floor", texture = floorChecker, ka = 0.55, kd = 0.85)
                // Bright far wall so transmitted rays exiting the bowl land on a non-black backdrop.
                matte(id = "wall", cd = c(0.9, 0.92, 0.96), ka = 0.8, kd = 0.7)
                // The fish: opaque orange, so its refraction and water tint are unmistakable.
                matte(id = "fish", cd = c(0.95, 0.45, 0.12), ka = 0.4, kd = 0.85)

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
            }

            objects {
                plane(material = "floor", point = p(0.0, -OUTER_RADIUS, 0.0), normal = Normal.UP)
                plane(material = "wall", point = p(0.0, 0.0, -7.0), normal = n(0, 0, 1))

                fishBowl(
                    glassAir = "glassAir",
                    waterAir = "waterAir",
                    waterGlass = "waterGlass",
                )

                // The fish: a small opaque sphere submerged below the water surface (y = 0.8), offset
                // sideways so its refraction and water tint through the glass are easy to read.
                sphere(material = "fish", center = p(-0.3, -0.4, 0.2), radius = FISH_RADIUS)
            }
        }
}
