package net.dinkla.raytracer.examples.objects

import net.dinkla.raytracer.objects.Torus
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object GreenTorus : WorldDefinition {
    override val id: String = "GreenTorus.kt"

    override fun world(): World =
        Builder.build {
            camera(d = 1500.0, eye = p(0, 10, 8), lookAt = p(2.75, -1.0, 0.0))

            ambientLight(ls = 0.25)

            lights {
                pointLight(location = p(0, 10, 5), ls = 1.0)
            }

            materials {
                matte(id = "gray", cd = c(1.0), ka = 0.25, kd = 0.75)
                reflective(id = "mirror", cd = c("0000FF"), ka = 0.0, kd = 1.0, ks = 1.0, kr = 1.0, cr = c(1.0, 0.0, 1.0))
                phong(id = "Green Yellow", cd = c("adff2f"), ka = 0.5, kd = 0.75, ks = 0.55, exp = 2.0)
                phong(id = "Light Salmon", cd = c("ffa07a"), ka = 0.5, kd = 0.75, ks = 0.55, exp = 15.0)
                phong(id = "Pink", cd = c("ffc0cb"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 9.0)
                phong(id = "Gold1", cd = c("FFD700"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 5.0)
                phong(id = "Gold2", cd = c("EEC900"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 5.0)
            }

            val t = Torus(a = 1.0, b = 0.05)
            objects {
                plane(material = "gray", point = p(0, -1, 0))
                instance(of = t, material = "Green Yellow") {
                    translate(v(1, 1, 1))
                    scale(v(2.0, 2.0, 2.0))
                }
            }
        }
}
