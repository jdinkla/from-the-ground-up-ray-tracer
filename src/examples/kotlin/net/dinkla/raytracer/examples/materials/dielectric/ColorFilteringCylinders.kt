package net.dinkla.raytracer.examples.materials.dielectric

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.compound.SolidCylinder
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * AC#2 for TASK-51: a **colour-filtering / Beer-Lambert demo** (Suffern, *Ray Tracing from the Ground
 * Up*, §28.4, Figure 28.8). Three [net.dinkla.raytracer.materials.Dielectric] cylinders with pure
 * **cyan / magenta / yellow** inside filter colours are laid end-on towards the camera and arranged in
 * an overlapping triangle (a Venn diagram) against a white background.
 *
 * Each cylinder transmits only the channels its filter passes (Beer's law: the inside filter colour
 * `cfIn` is raised to the path length, so `cf = 0` for a channel extinguishes it completely):
 *
 *  - **Cyan** `cfIn = (0, 1, 1)` absorbs red — the white backdrop reads cyan through it.
 *  - **Magenta** `cfIn = (1, 0, 1)` absorbs green.
 *  - **Yellow** `cfIn = (1, 1, 0)` absorbs blue.
 *
 * Where two cylinders overlap, a transmitted ray is filtered by both, so the channels multiply and
 * the overlap reads as the **subtractive secondary**: cyan×yellow = green, cyan×magenta = blue,
 * magenta×yellow = red. Where all three overlap every channel is killed and the region reads black.
 * This is the canonical subtractive-colour-mixing picture, produced here purely by stacked Beer's-law
 * attenuation rather than by painting the colours in.
 *
 * **Index of refraction.** The demo isolates *absorption*, not refraction, so the filters are given a
 * relative index just above 1 (`iorIn = 1.05`): a ray passes almost straight through each cylinder, so
 * the white wall behind reads through the filter as its colour and the overlap regions land where the
 * cylinders cross on screen. A high (glass/diamond) index would bend the transmitted rays off the
 * white wall and the subtractive mixing would not register — the colour filtering is the optical
 * content here, not the refraction.
 *
 * The cylinders are rotated 90° about x so their flat circular faces point at the camera (a
 * [SolidCylinder] otherwise stands on the y-axis), then translated into a triangular Venn layout in
 * the image plane. The background is a **white floor and white wall** (the book's white background):
 * without it the filtered light would read against black and the subtractive mixing would not be
 * visible.
 *
 * Uses the Whitted tracer (recursive reflection/refraction). This scene lives in the
 * coverage-excluded examples zone and is verified by rendering, not by unit tests.
 */
object ColorFilteringCylinders : WorldDefinition {
    override val id: String = "ColorFilteringCylinders.kt"

    // The cylinder lies along y from CYL_BACK..CYL_FRONT; after a 90° x-rotation this becomes its
    // depth extent (a stubby puck pointing at the camera).
    private const val CYL_BACK = -0.6
    private const val CYL_FRONT = 0.6
    private const val CYL_RADIUS = 1.6

    // Half-spacing of the three Venn centres around the image origin.
    private const val OFFSET = 0.95

    override fun world(): World =
        Builder.build {
            metadata {
                id(id)
                title("Colour-filtering cylinders (subtractive CMY, Beer-Lambert)")
                description(
                    "Three overlapping Dielectric cylinders with pure cyan/magenta/yellow filter colours " +
                        "over a white background (Suffern Fig 28.8). Overlaps read as the subtractive " +
                        "secondaries; the triple overlap reads black. Rendered with the Whitted tracer.",
                )
                preferredTracer(Tracers.WHITTED)
            }

            // Straight-on view so the three end-on cylinders form a clean Venn diagram in the frame.
            camera(d = 1300.0, eye = p(0.0, 0.0, 12.0), lookAt = p(0.0, 0.0, 0.0))

            ambientLight(ls = 0.85)

            lights {
                pointLight(location = p(6, 8, 10), ls = 1.0)
                pointLight(location = p(-6, 6, 9), ls = 0.7)
            }

            materials {
                // White floor and white wall: the book's white background for the filtering demo.
                matte(id = "floor", cd = c(0.98, 0.98, 0.98), ka = 0.85, kd = 0.7)
                matte(id = "wall", cd = c(0.99, 0.99, 0.99), ka = 0.9, kd = 0.7)

                // Pure subtractive primaries as Beer's-law inside filters; a zero channel is fully
                // absorbed. Near-index-matched (iorIn = 1.05) so rays pass nearly straight through.
                dielectric(
                    id = "cyan",
                    iorIn = 1.05,
                    iorOut = 1.0,
                    cfIn = c(0.0, 1.0, 1.0),
                    cfOut = c(1.0, 1.0, 1.0),
                    ka = 0.05,
                    kd = 0.05,
                    ks = 0.2,
                    cs = c(1.0, 1.0, 1.0),
                    exp = 4000.0,
                )
                dielectric(
                    id = "magenta",
                    iorIn = 1.05,
                    iorOut = 1.0,
                    cfIn = c(1.0, 0.0, 1.0),
                    cfOut = c(1.0, 1.0, 1.0),
                    ka = 0.05,
                    kd = 0.05,
                    ks = 0.2,
                    cs = c(1.0, 1.0, 1.0),
                    exp = 4000.0,
                )
                dielectric(
                    id = "yellow",
                    iorIn = 1.05,
                    iorOut = 1.0,
                    cfIn = c(1.0, 1.0, 0.0),
                    cfOut = c(1.0, 1.0, 1.0),
                    ka = 0.05,
                    kd = 0.05,
                    ks = 0.2,
                    cs = c(1.0, 1.0, 1.0),
                    exp = 4000.0,
                )
            }

            objects {
                plane(material = "floor", point = p(0.0, -3.0, 0.0), normal = Normal.UP)
                plane(material = "wall", point = p(0.0, 0.0, -3.0), normal = n(0, 0, 1))

                // One puck-shaped cylinder, instanced three times: rotate 90° about x to point each
                // flat face at the camera, then translate into a triangular Venn layout in the image
                // plane (cyan top, magenta lower-left, yellow lower-right).
                val puck = SolidCylinder(y0 = CYL_BACK, y1 = CYL_FRONT, radius = CYL_RADIUS)

                instance(material = "cyan", of = puck) {
                    rotate(Axis.X, 90.0)
                    translate(v(0.0, OFFSET, 0.0))
                }
                instance(material = "magenta", of = puck) {
                    rotate(Axis.X, 90.0)
                    translate(v(-OFFSET, -OFFSET, 0.0))
                }
                instance(material = "yellow", of = puck) {
                    rotate(Axis.X, 90.0)
                    translate(v(OFFSET, -OFFSET, 0.0))
                }
            }
        }
}
