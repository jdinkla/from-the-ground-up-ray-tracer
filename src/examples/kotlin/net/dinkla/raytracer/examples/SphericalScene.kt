package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Coverage example (TASK-40/41) for the [net.dinkla.raytracer.cameras.lenses.Spherical] panoramic lens
 * (Suffern ch. 11). The camera sits at the centre of a ring of coloured spheres with `maxLambda = 180°`
 * / `maxPsi = 90°`, unrolling the full 360°×180° surroundings across the frame: the ring spreads evenly
 * along the width, ground fills the lower band and sky the upper. Unlike [FishEyeScene] every pixel maps
 * to a ray, so there is no circular vignette.
 */
object SphericalScene : WorldDefinition {
    override val id: String = "SphericalScene.kt"

    private const val RING_COUNT = 12
    private const val RING_RADIUS = 20.0
    private const val EYE_HEIGHT = 6.0

    override fun world() =
        Builder.build {
            sphericalCamera(
                maxLambda = 180.0,
                maxPsi = 90.0,
                eye = p(0.0, EYE_HEIGHT, 0.0),
                lookAt = p(0.0, EYE_HEIGHT, -1.0),
            )

            ambientLight(color = Color.WHITE, ls = 0.5)

            lights {
                pointLight(location = p(0.0, 40.0, 0.0), ls = 3.0)
                pointLight(location = p(0.0, EYE_HEIGHT, 0.0), ls = 1.2, color = c(1.0, 0.95, 0.85))
            }

            val palette =
                listOf(
                    c(0.9, 0.2, 0.2), c(0.95, 0.6, 0.1), c(0.9, 0.85, 0.1),
                    c(0.2, 0.7, 0.3), c(0.2, 0.45, 0.85), c(0.7, 0.25, 0.8),
                )

            materials {
                matte(id = "ground", cd = c(0.5, 0.5, 0.55), ka = 0.35, kd = 0.7)
                palette.forEachIndexed { i, col -> phong(id = "ring$i", cd = col, ka = 0.4, kd = 0.7) }
            }

            objects {
                plane(material = "ground", point = Point3D.ORIGIN, normal = Normal.UP)
                for (i in 0 until RING_COUNT) {
                    val angle = 2.0 * PI * i / RING_COUNT
                    sphere(
                        material = "ring${i % palette.size}",
                        center = p(RING_RADIUS * cos(angle), EYE_HEIGHT, RING_RADIUS * sin(angle)),
                        radius = 4.0,
                    )
                }
            }
        }
}
