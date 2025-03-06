package net.dinkla.raytracer.examples.materials.reflective

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object World33 : WorldDefinition {
    override val id: String = "World33.kt"

    override fun world(): World =
        Builder.build {
            camera(d = 500.0, eye = p(3, 1, 5), lookAt = p(3, 0, 0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(0, 0, 5), ls = 1.0)
            }

            materials {
                matte(id = "sky", cd = c(0.3, 0.6, 1.0), ka = 1.0, kd = 0.9)
                phong(id = "grey", ks = 0.4, cd = c(0.7), ka = 0.25, kd = 0.6, exp = 10.0)
                phong(id = "r", ks = 1.0, cd = Color.RED, ka = 1.0, kd = 1.0, exp = 10.0)
                phong(id = "g", ks = 1.0, cd = Color.GREEN, ka = 1.0, kd = 1.0, exp = 20.0)
                phong(id = "b", ks = 1.0, cd = Color.BLUE, ka = 1.0, kd = 1.0, exp = 20.0)
                reflective(id = "rr", ks = 1.0, cd = Color.RED, ka = 1.0, kd = 1.0, exp = 10.0)
                reflective(id = "gr", ks = 1.0, cd = Color.GREEN, ka = 1.0, kd = 1.0, exp = 20.0)
                reflective(id = "br", ks = 1.0, cd = Color.BLUE, ka = 1.0, kd = 1.0, exp = 20.0)
            }

            objects {
                plane(material = "grey", point = p(0, -1, 0), normal = n(0, 1, 0))
                plane(material = "sky", point = p(0, 99999, 0), normal = n(0, -1, 0))

                sphere(material = "rr", center = p(1, 0, 1), radius = 0.9)
                sphere(material = "gr", center = p(3, 0, 1), radius = 0.9)
                sphere(material = "br", center = p(5, 0, 1), radius = 0.9)
            }
        }
}
