package net.dinkla.raytracer.examples.materials.transparent

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.compound.Bowl
import net.dinkla.raytracer.objects.compound.SolidCylinder
import net.dinkla.raytracer.objects.compound.ThickRing
import net.dinkla.raytracer.textures.PlaneChecker
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * TASK-50 AC#4: transparent compound objects — a solid cylinder, a thick ring, and a hemispherical
 * bowl, all at glass index `ior = 1.5` — Suffern Fig 27.19 (§27.8).
 *
 * These are the three compound objects of Fig 27.18/27.19, built from part-spheres and (concave/convex)
 * open cylinders with outward-pointing normals so the simple Ch. 27 transparency model traces them
 * correctly. The point of the figure is that all three **exhibit total internal reflection off their
 * inside surfaces** — dark regions appear on the curved walls where rays bounce around inside the glass
 * and never escape within the recursion depth (most visible on the cylinder's and ring's inner walls
 * and inside the bowl). The world's default maximal recursion depth of 5 leaves these effects visible
 * while letting the bulk of the transmitted light through.
 *
 * Each object has a red phong sphere behind it so the refracted/inverted transmission is unmistakable,
 * over a white checker floor with a non-black teal backdrop (matching the figure and the book's hint to
 * avoid a black background). The thick ring is stood up (rotated 90° about x) so its hole faces the
 * camera. Rendered with the Whitted tracer. This scene lives in the coverage-excluded examples zone and
 * is verified by rendering, not by unit tests.
 */
object TransparentCompoundObjects : WorldDefinition {
    override val id: String = "TransparentCompoundObjects.kt"

    // Crown-glass index of refraction (Suffern Fig 27.19: eta = 1.5).
    private const val GLASS_IOR = 1.5

    override fun world(): World =
        Builder.build {
            metadata {
                id(id)
                title("Transparent compound objects: cylinder, thick ring, bowl")
                description(
                    "A transparent solid cylinder, thick ring and hemispherical bowl (ior = 1.5) over a " +
                        "checker floor — Suffern Fig 27.19. All three show total internal reflection off " +
                        "their inside surfaces as dark regions on the curved walls.",
                )
                preferredTracer(Tracers.WHITTED)
            }

            camera(d = 1300.0, eye = p(0.0, 4.0, 13.0), lookAt = p(0.0, 0.3, 0.0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(8, 13, 10), ls = 1.0)
                pointLight(location = p(-10, 8, 7), ls = 0.6)
            }

            val floorChecker =
                PlaneChecker(
                    size = 1.5,
                    lineWidth = 0.0,
                    color1 = c(0.95, 0.95, 0.95),
                    color2 = c(0.45, 0.45, 0.45),
                    lineColor = c(0.45, 0.45, 0.45),
                )

            materials {
                svMatte(id = "floor", texture = floorChecker, ka = 0.5, kd = 0.85)
                matte(id = "backdrop", cd = c(0.10, 0.42, 0.42), ka = 0.8, kd = 0.7)
                phong(id = "red", cd = c(0.85, 0.15, 0.15), ka = 0.35, kd = 0.7, ks = 0.5, exp = 60.0)
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

            // The thick ring lies coaxial with the y-axis; standing it up presents its hole to the camera.
            val ring = ThickRing(y0 = -0.6, y1 = 0.6, innerRadius = 0.7, outerRadius = 1.1)

            objects {
                plane(material = "floor", point = p(0.0, -1.0, 0.0), normal = Normal.UP)
                plane(material = "backdrop", point = p(0.0, 0.0, -8.0), normal = n(0, 0, 1))

                // Red spheres sitting behind/inside each object so the transmission through the glass shows.
                sphere(material = "red", center = p(-3.6, -0.2, -2.2), radius = 1.0)
                sphere(material = "red", center = p(0.0, 0.3, 0.0), radius = 0.6)
                sphere(material = "red", center = p(3.6, -0.4, -1.0), radius = 0.6)

                // Solid cylinder (capped) on the left — short and wide, like Suffern Fig 27.19(a), so the
                // checker and the red sphere behind it transmit through rather than being trapped by TIR.
                instance(material = "glass", of = SolidCylinder(y0 = -1.0, y1 = 0.2, radius = 1.2)) {
                    translate(v(-3.6, 0.0, 0.0))
                }

                // Thick ring (a capped tube) in the centre, stood up so the hole faces the camera.
                instance(material = "glass", of = ring) {
                    rotate(Axis.X, 90.0)
                    translate(v(0.0, 0.3, 0.0))
                }

                // Hemispherical bowl on the right (opens upward, sits on the floor).
                instance(material = "glass", of = Bowl(innerRadius = 0.9, outerRadius = 1.1)) {
                    translate(v(3.6, 0.0, 0.0))
                }
            }
        }
}
