package net.dinkla.raytracer.examples.objects.beveled

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object BeveledBoxesCity : WorldDefinition {
    override val id: String = "World69.kt"
    override fun world(): World = Builder.build {


        camera(d = 1500.0, eye = p(5.0, 1.5, 10.0), lookAt = p(5, 0, -5))

        ambientLight(ls = 0.5)

        lights {
            pointLight(location = p(0, 10, 5), ls = 1.0)
        }

        materials {
            matte(id = "sky", cd = c(0.1, 0.7, 1.0), ka = 0.75, kd = 1.0)
            matte(
                id = "gray", cd = c(1.0), ka = 0.25, kd = 0.75
            )
            reflective(id = "mirror", cd = c("0000FF"), ka = 0.0, kd = 0.8, ks = 0.3, kr = 0.4, cr = c(1.0, 0.0, 1.0))
            phong(id = "m1", cd = c("adff2f"), ka = 0.5, kd = 0.75, ks = 0.55, exp = 2.0)
            phong(id = "m2", cd = c("ffa07a"), ka = 0.5, kd = 0.75, ks = 0.55, exp = 15.0)
            phong(id = "m3", cd = c("ffc0cb"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 9.0)
            phong(id = "m4", cd = c("FFD700"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 5.0)
            phong(id = "m5", cd = c("EEC900"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 5.0)
        }

        objects {
            plane(material = "gray")
            plane(material = "sky", point = p(0.0, 99999.0, 0.0), normal = Normal.DOWN)
            grid {
                repeat(50) { i ->
                    val x = rand() * (i % 20)
                    val y = 0.0
                    val z = rand() * -1 * (i % 20)
                    val x2 = x + rand()
                    val y2 = y + 2 * rand()
                    val z2 = z + rand()
                    beveledBox(p0 = p(x, y, z), p1 = p(x2, y2, z2), rb = 0.01, material = "m${(i % 5) + 1}")
                }
            }
        }
    }
}

private fun rand() = r.nextDouble()
private val r = java.util.Random()