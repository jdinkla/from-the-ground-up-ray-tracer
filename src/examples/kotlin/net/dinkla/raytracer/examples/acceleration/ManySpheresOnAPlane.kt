package net.dinkla.raytracer.examples.acceleration

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition
import net.dinkla.raytracer.world.rand
import kotlin.math.pow

const val NUM_SPHERES = 10000
const val VOLUME = 0.1 / NUM_SPHERES
private val radius = (0.75 * VOLUME / Math.PI).pow(1.0 / 3)

object ManySpheresOnAPlane : WorldDefinition {
    override val id: String = "ManySpheresOnAPlane.kt"

    override fun world(): World =
        Builder.build {
            camera(d = 1500.0, eye = p(1, 2, 10), lookAt = p(0, 0, 0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(2, 2, 0), ls = 3.141, color = c(0.3, 1.0, 0.3))
                pointLight(location = p(-2, 2, 0), ls = 3.141, color = c(0.3, 0.3, 1.0))
            }

            materials {
                matte(id = "ground", cd = c(0.8, 0.8, 0.8))
                matte(id = "sky", cd = Color.fromString("87ceeb"))
                repeat(NUM_SPHERES) {
                    val exp = 50 * rand()
                    val ks = rand()
                    val col = c(rand(), rand(), rand())
                    phong(id = "p$it", cd = col, ka = 0.25, kd = 0.75, exp = exp, ks = ks)
                }
            }

            objects {
                plane(point = p(0.0, -1.0 - radius, 0.0), material = "ground")
                plane(point = p(0.0, 5.0, 0.0), material = "sky")
                grid {
                    repeat(NUM_SPHERES) {
                        val cent = p(1.0 - 2.0 * rand(), 1.0 - 2.0 * rand(), 1.0 - 2.0 * rand())
                        sphere(center = cent, radius = radius, material = "p$it")
                    }
                }
            }
        }
}
