package net.dinkla.raytracer.examples.globalillumination

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * A reflective caustic for the path tracer (Suffern ch. 26, Figure 26.8). Render it with
 * `--tracer=PATH_TRACE`:
 *
 * ```
 * ./gradlew run --args="--world=ReflectiveCaustic.kt --tracer=PATH_TRACE --renderer=FORK_JOIN --resolution=720p"
 * ```
 *
 * The scene is the minimal specular-to-diffuse light-transport demo:
 *  - a white **matte floor** (`y = 0`) — the diffuse surface that catches the caustic,
 *  - a small **emissive sphere** floating above the floor — the scene's only light source,
 *  - a **flat mirror** standing vertically at the back (`z = 0`, facing the camera).
 *
 * Light leaving the emissive sphere reflects off the flat mirror and lands on the floor, so a bright
 * streak — the *reflective caustic* — appears on the floor where the mirrored light focuses, and the
 * sphere's reflection is visible in the mirror. This transport (light → specular bounce → diffuse
 * surface → eye) only exists once [net.dinkla.raytracer.materials.Reflective.pathShade] lets the path
 * tracer follow a ray through the mirror (TASK-46); before that the mirror rendered as a flat grey
 * diffuse panel and there was no caustic.
 *
 * The plain direct-lighting tracers cannot produce this effect — it is a global-illumination feature.
 */
object ReflectiveCaustic : WorldDefinition {
    override val id: String = "ReflectiveCaustic.kt"

    private const val LIGHT_INTENSITY = 30.0

    override fun world(): World =
        Builder.build {
            metadata {
                title("Reflective Caustic")
                description("Path-traced specular-to-diffuse transport: an emissive sphere casts a caustic via a flat mirror.")
                preferredTracer(Tracers.PATH_TRACE)
            }

            // The eye looks slightly down at the floor and toward the back mirror (down -z).
            camera(d = 480.0, eye = p(0.0, 130.0, 320.0), lookAt = p(0.0, 50.0, 0.0))

            // Ambient is irrelevant to the path tracer (it integrates indirect light directly).
            ambientLight(ls = 0.0)

            materials {
                matte(id = "floor", cd = c(0.75, 0.75, 0.75), ka = 0.0, kd = 1.0)
                reflective(
                    id = "mirror",
                    cd = c(0.0, 0.0, 0.0),
                    ka = 0.0,
                    kd = 0.0,
                    ks = 0.0,
                    kr = 1.0,
                    cr = c(1.0, 1.0, 1.0),
                )
                emissive(id = "light", ce = c(1.0, 0.9, 0.7), le = LIGHT_INTENSITY)
            }

            objects {
                // Matte floor at y = 0, normal up (faces the scene).
                plane(material = "floor", point = p(0.0, 0.0, 0.0), normal = Normal.UP)

                // Flat mirror standing vertically at the back (z = 0), facing the camera (+z).
                plane(material = "mirror", point = p(0.0, 0.0, 0.0), normal = Normal.FORWARD)

                // The only light: a small emissive sphere floating in front of the mirror.
                sphere(material = "light", center = p(-70.0, 90.0, 120.0), radius = 22.0)
            }
        }
}
