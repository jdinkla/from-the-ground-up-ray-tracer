package net.dinkla.raytracer.examples.materials.transparent

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.Torus
import net.dinkla.raytracer.textures.PlaneChecker
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * TASK-50 AC#3 (torus): a transparent torus at glass index `ior = 1.5` — Suffern Fig 27.29 /
 * Exercise 27.8.
 *
 * A torus is a closed transparent object, so the simple model of Ch. 27 traces it correctly. The
 * distinctive feature the book points out is the **black strips** at the top and bottom of the torus:
 * those are regions where rays are totally internally reflected off the torus's inside surfaces and,
 * within the maximal recursion depth, never escape to pick up scene radiance — so they read as dark
 * bands. The world's default maximal recursion depth of 5 lets most rays through while leaving the
 * characteristic strips visible.
 *
 * A red phong sphere sits in the torus's hole so the refraction/inversion through the glass ring is
 * unmistakable, over a white checker floor with a non-black teal backdrop (matching the book's figure
 * and its hint to avoid a black background for transparent objects). Rendered with the Whitted tracer.
 * This scene lives in the coverage-excluded examples zone and is verified by rendering, not by unit
 * tests.
 */
object TransparentTorus : WorldDefinition {
    override val id: String = "TransparentTorus.kt"

    // Crown-glass index of refraction (Suffern Fig 27.29: eta = 1.5).
    private const val GLASS_IOR = 1.5

    override fun world(): World =
        Builder.build {
            metadata {
                id(id)
                title("Transparent torus (ior = 1.5)")
                description(
                    "A transparent glass torus (ior = 1.5) over a checker floor with a red sphere in its " +
                        "hole — Suffern Fig 27.29 / Exercise 27.8. Note the characteristic black strips at " +
                        "the top and bottom, caused by total internal reflection off the inside surfaces.",
                )
                preferredTracer(Tracers.WHITTED)
            }

            camera(d = 1500.0, eye = p(0.0, 3.0, 9.0), lookAt = p(0.0, 0.4, 0.0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(7, 10, 8), ls = 1.0)
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
                    ior = GLASS_IOR,
                    kr = 0.1,
                    kt = 0.9,
                )
            }

            // A torus lies flat in the xz-plane; standing it up (rotate 90° about x) presents the ring
            // face-on to the camera, like Suffern Fig 27.29.
            val ring = Torus(a = 1.6, b = 0.5)

            objects {
                plane(material = "floor", point = p(0.0, -1.0, 0.0), normal = Normal.UP)
                plane(material = "backdrop", point = p(0.0, 0.0, -7.0), normal = n(0, 0, 1))
                // Red sphere nestled in the torus hole so the refracted/inverted view through the ring shows.
                sphere(material = "red", center = p(0.0, 0.4, 0.0), radius = 0.8)
                instance(material = "glass", of = ring) {
                    rotate(Axis.X, 90.0)
                    translate(v(0.0, 0.4, 0.0))
                }
            }
        }
}
