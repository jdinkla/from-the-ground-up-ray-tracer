package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.MathUtils.PI
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition
import kotlin.math.pow
import kotlin.random.Random

object World11 : WorldDefinition {
    override val id: String = "World11.kt"
    private const val numSpheres = 25
    private const val volume = 0.1 / numSpheres
    private val radius = (0.75 * volume / PI).pow(1.0 / 3.0)
    private val r = Random.Default

    override fun world() = Builder.build {
        camera(d = 1500.0, eye = p(1, 2, 10), lookAt = p(0, 0, 0))

        ambientLight(ls = 0.5)

        lights {
            pointLight(location = p(2, 2, 0), ls = 3.141, color = c(0.3, 1.0, 0.3))
            pointLight(location = p(-2, 2, 0), ls = 3.141, color = c(0.3, 0.3, 1.0))
        }

        materials {
            matte(id = "m1", cd = c(0.9, 0.4, 0.4))
            matte(id = "m2", cd = c(0.8, 0.8, 0.8))

            for (i in 0 until numSpheres) {
                val exp = (r.nextDouble() * 50)
                val ks = r.nextDouble()
                val col = Color(r.nextDouble(), r.nextDouble(), r.nextDouble())
                phong(id = "p$i", cd = col, ka = 0.25, kd = 0.75, exp = exp, ks = ks)
            }
        }

        objects {
            rectangle(p0 = p(0, -1, 0), a = v(1, 0, 0), b = v(0, 0, 1), material = "m1")
            rectangle(p0 = p(-1, -1, -1), a = v(1, 0, 0), b = v(0, 0, 1), material = "m1")
            plane(point = p(0.0, -1.0 - radius, 0.0), material = "m2")

            grid {
                for (i in 0 until numSpheres) {
                    val cent = p(1.0 - 2.0 * r.nextFloat(), 1.0 - 2.0 * r.nextFloat(), 1.0 - 2.0 * r.nextFloat())
                    sphere(center = cent, radius = radius, material = "p$i")
                }
            }
        }
    }
}
