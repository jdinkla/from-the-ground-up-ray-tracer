package net.dinkla.raytracer.examples.globalillumination

import net.dinkla.raytracer.materials.Emissive
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.arealights.RectangleLight
import net.dinkla.raytracer.samplers.PureRandom
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * A Cornell-box scene for the hybrid global tracer (Suffern ch. 26, section 26.4). Render it with
 * `--tracer=GLOBAL_TRACE`:
 *
 * ```
 * ./gradlew run --args="--world=CornellBoxGlobal.kt --tracer=GLOBAL_TRACE --renderer=FORK_JOIN --resolution=720p"
 * ```
 *
 * It is the same geometry as [CornellBox], but its ceiling light is declared **twice**, to feed the
 * two transport terms the [net.dinkla.raytracer.tracers.GlobalTrace] tracer separates:
 *
 *  - as a [RectangleLight] area light (in `lights { }`), so the direct illumination is computed by
 *    *sampling the light* at the first hit (Listing 26.7) — analytic, low-noise; and
 *  - as an emissive rectangle (in `objects { }`) at the same place, so the *indirect* path bounces
 *    (depth >= 2) still see the panel as a light source. The radiance-flow rules
 *    ([Emissive.globalShade], Listing 26.6) suppress its emission on the first indirect bounce so the
 *    direct light is not counted twice (Fig 26.11).
 *
 * Compared with rendering the same box under `--tracer=PATH_TRACE` at the same sample count, the
 * directly-lit floor, walls and box faces are markedly less noisy (book Fig 26.12), because the direct
 * term no longer relies on random paths happening to hit the small ceiling light.
 */
object CornellBoxGlobal : WorldDefinition {
    override val id: String = "CornellBoxGlobal.kt"

    private const val BOX = 555.0
    private const val LIGHT_INTENSITY = 24.0
    private const val NUM_LIGHT_SAMPLES = 32

    override fun world(): World =
        Builder.build {
            metadata {
                title("Cornell Box (global trace)")
                description("Hybrid global illumination: direct light sampling + path-traced indirect.")
                preferredTracer(Tracers.GLOBAL_TRACE)
            }

            // The eye looks down -z into the open front of the box.
            camera(d = 600.0, eye = p(278.0, 278.0, -800.0), lookAt = p(278.0, 278.0, 0.0))

            // Ambient is irrelevant to the global tracer (it integrates direct + indirect light directly).
            ambientLight(ls = 0.0)

            materials {
                matte(id = "white", cd = c(0.73, 0.73, 0.73), ka = 0.0, kd = 1.0)
                matte(id = "red", cd = c(0.65, 0.05, 0.05), ka = 0.0, kd = 1.0)
                matte(id = "green", cd = c(0.12, 0.45, 0.15), ka = 0.0, kd = 1.0)
                emissive(id = "light", ce = c(1.0, 1.0, 1.0), le = LIGHT_INTENSITY)
            }

            // The ceiling panel as an area light, sampled for low-noise direct illumination. Same corner
            // and edge vectors as the emissive rectangle below; normal points down into the box.
            val lightSampler = Sampler(PureRandom, 100, 100).apply { mapSamplesToUnitDisk() }
            val rectangleLight =
                RectangleLight(
                    sampler = lightSampler,
                    p0 = p(213.0, 554.0, 227.0),
                    a = v(130.0, 0.0, 0.0),
                    b = v(0.0, 0.0, 105.0),
                    normal = Normal.DOWN,
                ).apply {
                    material = Emissive(ls = LIGHT_INTENSITY)
                }

            lights {
                areaLight(of = rectangleLight, numSamples = NUM_LIGHT_SAMPLES)
            }

            objects {
                // Side walls: red on the left, green on the right (normals face into the box).
                plane(material = "red", point = p(0.0, 0.0, 0.0), normal = Normal.RIGHT)
                plane(material = "green", point = p(BOX, 0.0, 0.0), normal = Normal.LEFT)

                // Floor, ceiling and back wall (white), all facing inward.
                plane(material = "white", point = p(0.0, 0.0, 0.0), normal = Normal.UP)
                plane(material = "white", point = p(0.0, BOX, 0.0), normal = Normal.DOWN)
                plane(material = "white", point = p(0.0, 0.0, BOX), normal = Normal.BACKWARD)

                // Emissive ceiling panel; a cross b = (0,-1,0) so its normal points down into the box.
                // Kept so the indirect path bounces (depth >= 2) still see the light source.
                rectangle(
                    material = "light",
                    p0 = p(213.0, 554.0, 227.0),
                    a = v(130.0, 0.0, 0.0),
                    b = v(0.0, 0.0, 105.0),
                )

                // A tall box at the back-left and a short box at the front-right.
                box(
                    material = "white",
                    p0 = p(130.0, 0.0, 230.0),
                    a = v(160.0, 0.0, 0.0),
                    b = v(0.0, 330.0, 0.0),
                    c = v(0.0, 0.0, 160.0),
                )
                box(
                    material = "white",
                    p0 = p(290.0, 0.0, 70.0),
                    a = v(160.0, 0.0, 0.0),
                    b = v(0.0, 165.0, 0.0),
                    c = v(0.0, 0.0, 160.0),
                )
            }
        }
}
