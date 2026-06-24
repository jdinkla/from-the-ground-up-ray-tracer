package net.dinkla.raytracer.examples.materials.transparent

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.textures.PlaneChecker
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * TASK-50 AC#3 (ellipsoid): a transparent ellipsoid — a unit [Sphere] scaled by an [Instance] — at
 * relative `eta = 0.75`, after Suffern Figs 27.24/27.28 (§27.7, Exercise 27.7).
 *
 * The ellipsoid is built exactly as the book describes: it is rendered with **instancing**, so the
 * inverse-transformed rays are intersected with a generic unit sphere. The optical interest is
 * twofold:
 *  - With `eta = 0.75` (the air-bubble index) the ellipsoid shows a thin **black strip** around its
 *    silhouette, where grazing rays are totally internally reflected off the inside surface and, within
 *    the recursion depth, never escape (Fig 27.24(a)).
 *  - Because the sphere is non-uniformly scaled, the specular highlights are **elongated** along the
 *    long axis (Fig 27.28) — the test the book uses to confirm transparency works with transformed
 *    objects (Exercise 27.7).
 *
 * A red phong sphere behind the ellipsoid makes the inverting transmission visible, over a white
 * checker floor with a non-black teal backdrop (matching the figure and the book's no-black-background
 * hint). Rendered with the Whitted tracer. This scene lives in the coverage-excluded examples zone and
 * is verified by rendering, not by unit tests.
 */
object TransparentEllipsoid : WorldDefinition {
    override val id: String = "TransparentEllipsoid.kt"

    // Relative index of refraction eta = 0.75 (Suffern Fig 27.28), giving the silhouette black strip.
    private const val ELLIPSOID_ETA = 0.75

    override fun world(): World =
        Builder.build {
            metadata {
                id(id)
                title("Transparent ellipsoid (scaled sphere, eta = 0.75)")
                description(
                    "A transparent ellipsoid built as an instanced (non-uniformly scaled) unit sphere at " +
                        "eta = 0.75 — Suffern Figs 27.24/27.28. Note the thin black strip around its " +
                        "silhouette (TIR off the inside surface) and the elongated specular highlights.",
                )
                preferredTracer(Tracers.WHITTED)
            }

            camera(d = 1500.0, eye = p(0.0, 3.0, 10.0), lookAt = p(0.0, 0.3, 0.0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(7, 11, 9), ls = 1.0)
                pointLight(location = p(-8, 6, 5), ls = 0.6)
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
                    ior = ELLIPSOID_ETA,
                    kr = 0.1,
                    kt = 0.9,
                )
            }

            // The ellipsoid: a unit sphere scaled non-uniformly via an Instance (Suffern's instancing).
            val unitSphere = Sphere(radius = 1.0)

            objects {
                plane(material = "floor", point = p(0.0, -1.0, 0.0), normal = Normal.UP)
                plane(material = "backdrop", point = p(0.0, 0.0, -7.0), normal = n(0, 0, 1))
                sphere(material = "red", center = p(0.0, 0.3, -3.0), radius = 0.9)
                instance(material = "glass", of = unitSphere) {
                    scale(v(2.2, 1.1, 1.1))
                    translate(v(0.0, 0.3, 0.0))
                }
            }
        }
}
