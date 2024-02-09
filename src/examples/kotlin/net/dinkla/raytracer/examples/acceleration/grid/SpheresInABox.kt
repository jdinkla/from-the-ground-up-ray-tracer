package net.dinkla.raytracer.examples.acceleration.grid

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition
import net.dinkla.raytracer.world.rand
import net.dinkla.raytracer.world.repeat3

object SpheresInABox : WorldDefinition {
    override val id: String = "SpheresInABox.kt"

    const val NUM = 25.0
    const val column = NUM / 2 - 0.15

    override fun world(): World = Builder.build {
        metadata {
            description("This may take a while on slower machines.")
        }

        camera(d = 1000.0, eye = p(-1.0, NUM, NUM + 5), lookAt = p(column, NUM - NUM / 5, column))

        ambientLight(ls = 0.5)

        lights {
            pointLight(location = p(NUM, NUM * 2, NUM * 2), ls = 1.0)
        }

        materials {
            reflective(id = "grey", ks = 1.0, cd = c(0.1, 0.1, 0.1), ka = 0.0, kd = 1.0, exp = 10.0)
            repeat3(NUM.toInt()) { i, j, k ->
                val exp = rand() * 50
                val color = c(rand(), rand(), rand())
                reflective(
                    id = "c$i-$j-$k",
                    ka = 0.5,
                    kd = 0.5,
                    cd = color,
                    ks = 1.0,
                    cs = color,
                    kr = 1.0,
                    cr = color,
                    exp = exp
                )
            }
        }

        objects {
            plane(material = "grey", point = p(0, 0, -5), normal = Normal.FORWARD)
            plane(material = "grey", point = p(0, -5, 0), normal = Normal.UP)
            plane(material = "grey", point = p(NUM + 5, 0.0, 0.0), normal = Normal.LEFT)
            plane(material = "grey", point = p(0.0, NUM * 2 + 5, 0.0), normal = Normal.DOWN)
            plane(material = "grey", point = p(0.0, 0.0, NUM * 2 + 5), normal = Normal.BACKWARD)
            plane(material = "grey", point = p(-NUM - 5, 0.0, 0.0), normal = Normal.RIGHT)
            grid {
                repeat3(NUM.toInt()) { i, j, k ->
                    sphere(center = p(i, j, k), radius = 0.25, material = "c$i-$j-$k")
                }
            }
        }
    }
}
