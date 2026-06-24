package net.dinkla.raytracer.examples.globalillumination

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * A refractive caustic for the path tracer (Suffern ch. 28 §28.9, Figure 28.42). Render it with
 * `--tracer=PATH_TRACE`:
 *
 * ```
 * ./gradlew run --args="--world=RefractiveCaustic.kt --tracer=PATH_TRACE --renderer=FORK_JOIN --resolution=720p"
 * ```
 *
 * The scene is the minimal refraction-to-diffuse light-transport demo:
 *  - a white **matte floor** (`y = 0`) — the diffuse surface that catches the caustic,
 *  - a red-tinted **dielectric (glass) sphere** sitting just above the floor,
 *  - a bright **emissive ceiling panel** that drives the caustic, and
 *  - a softly self-lit warm **back wall** behind the sphere, so the scene refracted through the glass
 *    is visible (the central rays transmit the inverted, magnified backdrop; the rim is reflective).
 *
 * Light leaving the ceiling panel refracts through the glass sphere, which focuses it onto the floor
 * below — a bright, concentrated **refractive caustic**, tinted red by the sphere's inside filter
 * colour ([net.dinkla.raytracer.materials.Dielectric] `cfIn`, Beer's-law attenuation). The book notes
 * this transport (light → refraction → diffuse surface → eye) can only be rendered with path tracing
 * (or photon mapping); the plain direct-lighting tracers cannot produce it.
 *
 * This effect only exists once [net.dinkla.raytracer.materials.Dielectric.pathShade] (and
 * [net.dinkla.raytracer.materials.Transparent.pathShade]) let the path tracer follow a ray through the
 * glass (TASK-47); before that a transparent/dielectric object rendered black under PATH_TRACE and
 * there was no caustic.
 *
 * Expect Monte-Carlo noise — the book uses 256 samples/pixel; here the path tracer averages its
 * default number of primary paths, so the caustic reads as a noisy bright patch on the floor.
 */
object RefractiveCaustic : WorldDefinition {
    override val id: String = "RefractiveCaustic.kt"

    private const val BOX = 555.0
    private const val CEILING_INTENSITY = 90.0
    private const val WALL_INTENSITY = 4.0

    override fun world(): World =
        Builder.build {
            metadata {
                title("Refractive Caustic")
                description("Path-traced refraction-to-diffuse transport: a glass sphere focuses light into a caustic.")
                preferredTracer(Tracers.PATH_TRACE)
            }

            // The eye looks down -z into the open front of the box, low and close so the floor caustic
            // beneath the sphere and the scene refracted through the sphere both fill the frame.
            camera(d = 520.0, eye = p(278.0, 180.0, -680.0), lookAt = p(278.0, 95.0, 0.0))

            // Ambient is irrelevant to the path tracer (it integrates indirect light directly).
            ambientLight(ls = 0.0)

            materials {
                matte(id = "floor", cd = c(0.85, 0.85, 0.85), ka = 0.0, kd = 1.0)
                matte(id = "side", cd = c(0.65, 0.65, 0.65), ka = 0.0, kd = 1.0)
                // The ceiling panel that drives the floor caustic.
                emissive(id = "ceiling", ce = c(1.0, 1.0, 1.0), le = CEILING_INTENSITY)
                // A softly self-lit warm back wall directly behind the sphere: a camera ray that
                // refracts through the glass exits straight onto this wall, so the inverted, magnified
                // image seen *through* the sphere is bright and unmistakable without depending on rare
                // light-finding paths. A warm (not blue) tint so the red glass transmits rather than
                // absorbs it — red glass would otherwise eat a blue backdrop (Beer's law). Modest
                // intensity so it reads as a coloured backdrop, not a lamp.
                emissive(id = "backWall", ce = c(0.95, 0.80, 0.55), le = WALL_INTENSITY)
                dielectric(
                    id = "glass",
                    iorIn = 1.5,
                    iorOut = 1.0,
                    // Red inside filter: rays deepen toward red with path length through the glass, so
                    // the focused caustic on the floor is tinted red (Beer's law in the path tracer).
                    // Green/blue are attenuated but not extinguished, so the warm backdrop is still
                    // visible (reddened) through the sphere's central transmitting region.
                    cfIn = c(0.95, 0.45, 0.45),
                    cfOut = c(1.0, 1.0, 1.0),
                    ka = 0.0,
                    kd = 0.0,
                    ks = 0.0,
                    exp = 4000.0,
                )
            }

            objects {
                // White matte floor (catches the caustic), grey side walls and ceiling backing, a
                // self-lit warm back wall (the scene refracted through the sphere). All face inward.
                plane(material = "floor", point = p(0.0, 0.0, 0.0), normal = Normal.UP)
                plane(material = "side", point = p(0.0, BOX, 0.0), normal = Normal.DOWN)
                plane(material = "backWall", point = p(0.0, 0.0, BOX), normal = Normal.BACKWARD)
                plane(material = "side", point = p(0.0, 0.0, 0.0), normal = Normal.RIGHT)
                plane(material = "side", point = p(BOX, 0.0, 0.0), normal = Normal.LEFT)

                // Large, bright emissive ceiling panel — the caustic's light source.
                rectangle(
                    material = "ceiling",
                    p0 = p(158.0, 554.0, 172.0),
                    a = v(240.0, 0.0, 0.0),
                    b = v(0.0, 0.0, 215.0),
                )

                // The glass sphere, sitting just above the floor so its refracted light focuses into a
                // tight, bright caustic on the floor directly beneath it.
                sphere(material = "glass", center = p(278.0, 110.0, 300.0), radius = 95.0)
            }
        }
}
