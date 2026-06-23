package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition

/**
 * Coverage example (TASK-42) for the [net.dinkla.raytracer.objects.acceleration.SparseGrid]
 * acceleration structure: a 6×6 field of spheres collected into a `sparseGrid { }`, the [Grid] variant
 * that stores only its non-empty cells. The structure is transparent to rendering — the scene looks
 * like any grid of spheres — but the audit's geometry walk records the SparseGrid wrapper.
 */
object SparseGridScene : WorldDefinition {
    override val id: String = "SparseGridScene.kt"

    private const val GRID_N = 6
    private const val SPACING = 2.0
    private const val OFFSET = (GRID_N - 1) / 2.0 * SPACING

    override fun world() =
        Builder.build {
            camera(d = 1400.0, eye = p(13.0, 11.0, 16.0), lookAt = p(0.0, 0.0, 0.0))

            ambientLight(color = Color.WHITE, ls = 0.4)

            lights {
                pointLight(location = p(20, 30, 20), ls = 3.0)
                pointLight(location = p(-15, 15, 10), ls = 1.5, color = c(1.0, 0.9, 0.8))
            }

            val palette =
                listOf(
                    c(0.9, 0.2, 0.2), c(0.95, 0.6, 0.1), c(0.9, 0.85, 0.1),
                    c(0.2, 0.7, 0.3), c(0.2, 0.45, 0.85), c(0.7, 0.25, 0.8),
                )

            materials {
                matte(id = "ground", cd = c(0.55, 0.55, 0.6), ka = 0.3, kd = 0.7)
                palette.forEachIndexed { i, col -> phong(id = "s$i", cd = col, ka = 0.35, kd = 0.7) }
            }

            objects {
                plane(material = "ground", point = Point3D.ORIGIN, normal = Normal.UP)
                sparseGrid {
                    for (x in 0 until GRID_N) {
                        for (z in 0 until GRID_N) {
                            sphere(
                                material = "s${(x + z) % palette.size}",
                                center = p(x * SPACING - OFFSET, 0.9, z * SPACING - OFFSET),
                                radius = 0.85,
                            )
                        }
                    }
                }
            }
        }
}
