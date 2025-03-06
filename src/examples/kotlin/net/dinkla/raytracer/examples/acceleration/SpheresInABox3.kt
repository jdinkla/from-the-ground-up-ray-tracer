package net.dinkla.raytracer.examples.acceleration

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition
import net.dinkla.raytracer.world.rand
import net.dinkla.raytracer.world.repeat3

object SpheresInABox3 : WorldDefinition {
    override val id: String = "SpheresInABox3.kt"

    private const val NUM = 25
    private const val column = NUM / 2 - 0.15

    override fun world() =
        Builder.build {
            camera(d = 1000.0, eye = p(-1, NUM, NUM + 5), lookAt = p(column, NUM - NUM / 5.0, column))

            ambientLight(color = Color.WHITE, ls = 0.5)

            lights {
                pointLight(location = p(NUM, NUM * 2, NUM * 2), ls = 1.0)
            }

            materials {
                reflective(id = "grey", ks = 1.0, cd = c(0.1, 0.1, 0.1), ka = 0.0, kd = 1.0, exp = 10.0)
                repeat3(NUM) { i, j, k ->
                    val exp = rand() * 50
                    val col = c(rand(), rand(), rand())
                    val ka = 0.5
                    val kd = 0.5
                    val ks = 1.0
                    val kr = 1.0
                    reflective(
                        id = "c$i-$j-$k",
                        ka = ka,
                        kd = kd,
                        cd = col,
                        ks = ks,
                        cs = col,
                        kr = kr,
                        cr = col,
                        exp = exp,
                    )
                }
            }

            objects {
                plane(material = "grey", point = p(0, 0, -5), normal = n(0, 0, 1)) // hinten
                plane(material = "grey", point = p(0, -5, 0), normal = n(0, 1, 0)) // unten
                plane(material = "grey", point = p(NUM + 5, 0, 0), normal = n(-1, 0, 0)) // rechts hinten
                plane(material = "grey", point = p(0, NUM * 2 + 5, 0), normal = n(0, -1, 0)) // oben
                plane(material = "grey", point = p(0, 0, NUM * 2 + 5), normal = n(0, 0, -1)) // rechts vorne
                plane(material = "grey", point = p(-NUM - 5, 0, 0), normal = n(1, 0, 0)) // links
                grid {
                    repeat3(NUM) { i, j, k ->
                        sphere(center = p(i, j, k), radius = 0.25, material = "c$i-$j-$k")
                    }
                }
            }
        }
}
