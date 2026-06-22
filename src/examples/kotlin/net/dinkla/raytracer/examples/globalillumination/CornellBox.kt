package net.dinkla.raytracer.examples.globalillumination

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

/**
 * A Cornell-box scene for the path tracer (Suffern ch. 26). Render it with `--tracer=PATH_TRACE`:
 *
 * ```
 * ./gradlew run --args="--world=CornellBox.kt --tracer=PATH_TRACE --renderer=FORK_JOIN --resolution=720p"
 * ```
 *
 * The box spans `[0,555]^3` with the open side toward the camera (looking down `-z`):
 *  - red left wall (`x = 0`) and green right wall (`x = 555`)
 *  - white floor (`y = 0`), ceiling (`y = 555`) and back wall (`z = 0`)
 *  - a small emissive panel on the ceiling, the scene's only light source
 *  - two white matte boxes (a tall and a short one) standing on the floor
 *
 * With global illumination the white floor and boxes pick up a red/green tint from the side walls
 * (colour bleeding), the shadows are soft, and the indirect light fills the box. The plain
 * direct-lighting tracers cannot show these effects.
 */
object CornellBox : WorldDefinition {
    override val id: String = "CornellBox.kt"

    private const val BOX = 555.0
    private const val LIGHT_INTENSITY = 24.0

    override fun world(): World =
        Builder.build {
            metadata {
                title("Cornell Box")
                description("Path-traced global illumination: colour bleeding and soft indirect light.")
            }

            // The eye looks down -z into the open front of the box.
            camera(d = 600.0, eye = p(278.0, 278.0, -800.0), lookAt = p(278.0, 278.0, 0.0))

            // Ambient is irrelevant to the path tracer (it integrates indirect light directly).
            ambientLight(ls = 0.0)

            materials {
                matte(id = "white", cd = c(0.73, 0.73, 0.73), ka = 0.0, kd = 1.0)
                matte(id = "red", cd = c(0.65, 0.05, 0.05), ka = 0.0, kd = 1.0)
                matte(id = "green", cd = c(0.12, 0.45, 0.15), ka = 0.0, kd = 1.0)
                emissive(id = "light", ce = c(1.0, 1.0, 1.0), le = LIGHT_INTENSITY)
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
