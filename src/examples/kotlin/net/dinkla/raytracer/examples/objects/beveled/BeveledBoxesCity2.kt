package net.dinkla.raytracer.examples.objects.beveled

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition
import net.dinkla.raytracer.world.rand
import net.dinkla.raytracer.world.randInt

object BeveledBoxesCity2 : WorldDefinition {
    override val id: String = "BeveledBoxesCity2.kt"
    private const val NUM_COLORS = 4
    private const val NUM_OBJECTS = 1000

    override fun world(): World =
        Builder.build {
            camera(d = 850.0, eye = p(-1.0, 3.5, 5.0), lookAt = p(2.9, 0.0, 0.0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(0, 10, 5), ls = 1.0)
            }

            materials {
                matte(id = "gray", cd = c(1.0), ka = 0.25, kd = 0.75)
                matte(id = "green", cd = c("009933"), ka = 0.5, kd = 1.0)
                matte(id = "skyblue", cd = c("5CD6FF"), ka = 1.0, kd = 1.0)

                repeat(NUM_COLORS) { i ->
                    val f = 0.75
                    val fr = (1.0 - f) * rand() + f
                    val fg = (1.0 - f) * rand() + f
                    val fb = (1.0 - f) * rand() + f
                    val fka = rand()
                    val fkd = rand()
                    val fks = rand()
                    phong(id = "c-$i", cd = c(fr, fg, fb), ka = fka, kd = fkd, ks = fks, exp = 5.0)
                }
            }

            objects {
                plane(material = "green")
                plane(material = "skyblue", point = p(0, 100, 0), normal = Normal.DOWN)
                grid {
                    repeat(NUM_OBJECTS) { i ->
                        val x = rand() * (i % 50)
                        val y = 0.0
                        val z = rand() * -1 * (i % 50)
                        val x2 = x + rand()
                        val y2 = y + rand()
                        val z2 = z + rand()
                        val c = randInt(NUM_COLORS)
                        val mat = "c-$c"
                        alignedBox(p = p(x, y, z), q = p(x2, y2, z2), material = mat)
                    }
                }
            }
        }
}
