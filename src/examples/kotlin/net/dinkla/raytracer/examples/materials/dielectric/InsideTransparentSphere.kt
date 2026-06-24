package net.dinkla.raytracer.examples.materials.dielectric

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.textures.PlaneChecker
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * AC#3 for TASK-49: the camera sits **inside** a large transparent ([net.dinkla.raytracer.materials.Dielectric])
 * sphere and looks out at a ring of coloured spheres standing on a checkered floor — the interior
 * view of Suffern §28.6.3, Fig 28.34.
 *
 * The optical point of the scene: inside a dense medium the radiance carried along each primary ray
 * is scaled by `(eta_in/eta_out)²` as it crosses the surface. Here the enclosing sphere uses
 * diamond's index (`iorIn = 2.42`), so the factor is about `2.42² ≈ 5.86` — bright enough to wash the
 * whole interior view out to white. The camera compensates with a reduced exposure
 * (`exposureTime = 1 / 2.42² ≈ 0.17`, Suffern's `1/eta²`), so the surrounding scene reads at normal
 * brightness instead of being blown out. Rendering this scene at the default `exposureTime = 1.0`
 * would show an over-exposed, near-white frame; the reduced value is what makes the interior view
 * legible.
 *
 * Uses the Whitted tracer (recursive reflection/refraction) — dielectrics need it. This scene lives
 * in the coverage-excluded examples zone and is verified by rendering, not by unit tests.
 */
object InsideTransparentSphere : WorldDefinition {
    override val id: String = "InsideTransparentSphere.kt"

    // Diamond's index of refraction; the squared ratio (~5.86) is the radiance scale across the surface.
    private const val DIAMOND_IOR = 2.42

    // Suffern's 1/eta^2 exposure compensation for a camera inside the dense medium.
    private const val INSIDE_EXPOSURE = 1.0 / (DIAMOND_IOR * DIAMOND_IOR)

    private const val RING_COUNT = 8
    private const val RING_RADIUS = 6.0
    private const val SPHERE_RADIUS = 1.2
    private const val SPHERE_Y = 0.2

    override fun world(): World =
        Builder.build {
            metadata {
                id(id)
                title("Camera inside a transparent (diamond) sphere")
                description(
                    "A view from inside a large dielectric sphere onto a ring of spheres over a checker " +
                        "floor (Suffern Fig 28.34). The camera uses a reduced exposureTime (1/eta^2) so the " +
                        "interior view is not washed out by the (eta_in/eta_out)^2 radiance scaling.",
                )
                preferredTracer(Tracers.WHITTED)
            }

            // Camera at the centre of the enclosing sphere, looking out across the ring.
            camera(
                d = 600.0,
                eye = p(0.0, 1.0, 0.0),
                lookAt = p(0.0, 0.6, -1.0),
                exposureTime = INSIDE_EXPOSURE,
            )

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(8, 12, 8), ls = 1.0)
                pointLight(location = p(-8, 10, -6), ls = 0.7)
            }

            val floorChecker =
                PlaneChecker(
                    size = 2.0,
                    lineWidth = 0.0,
                    color1 = c(0.92, 0.92, 0.92),
                    color2 = c(0.15, 0.25, 0.55),
                    lineColor = c(0.15, 0.25, 0.55),
                )

            materials {
                svMatte(id = "floor", texture = floorChecker, ka = 0.5, kd = 0.85)
                phong(id = "red", cd = c(0.85, 0.2, 0.2), ka = 0.3, kd = 0.7, ks = 0.4, exp = 50.0)
                phong(id = "green", cd = c(0.2, 0.75, 0.3), ka = 0.3, kd = 0.7, ks = 0.4, exp = 50.0)
                phong(id = "blue", cd = c(0.2, 0.35, 0.85), ka = 0.3, kd = 0.7, ks = 0.4, exp = 50.0)
                phong(id = "yellow", cd = c(0.9, 0.8, 0.2), ka = 0.3, kd = 0.7, ks = 0.4, exp = 50.0)
                // The enclosing transparent sphere; the camera is at its centre.
                dielectric(
                    id = "enclosure",
                    iorIn = DIAMOND_IOR,
                    iorOut = 1.0,
                    cfIn = c(0.95, 0.98, 1.0),
                    cfOut = c(1.0, 1.0, 1.0),
                    ka = 0.1,
                    kd = 0.1,
                    ks = 0.3,
                    cs = c(1.0, 1.0, 1.0),
                    exp = 4000.0,
                )
            }

            objects {
                plane(material = "floor", point = p(0.0, -1.0, 0.0), normal = Normal.UP)

                // A ring of coloured spheres standing on the floor, cycling through the four materials.
                val palette = listOf("red", "green", "blue", "yellow")
                for (i in 0 until RING_COUNT) {
                    val angle = 2.0 * PI * i / RING_COUNT
                    val cx = RING_RADIUS * cos(angle)
                    val cz = RING_RADIUS * sin(angle)
                    sphere(
                        material = palette[i % palette.size],
                        center = Point3D(cx, SPHERE_Y, cz),
                        radius = SPHERE_RADIUS,
                    )
                }

                // The enclosing transparent sphere surrounding the camera.
                sphere(material = "enclosure", center = p(0.0, 1.0, 0.0), radius = 3.0)
            }
        }
}
