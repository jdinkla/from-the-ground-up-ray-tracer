package net.dinkla.raytracer.examples.materials.reflective

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object World80 : WorldDefinition {
    override val id: String = "World80.kt"

    override fun world(): World =
        Builder.build {
            camera(d = 500.0, eye = p(3.0, 1.0, 5.0), lookAt = p(3.0, 0.0, 0.0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(0.0, 0.0, 5.0), ls = 1.0)
            }

            materials {
                matte(id = "sky", cd = c(0.3, 0.6, 1.0), ka = 1.0, kd = 0.9)
                phong(id = "grey", ks = 0.4, cd = c(0.7), ka = 0.25, kd = 0.6, exp = 10.0)
                phong(id = "r", ks = 1.0, cd = Color.RED, ka = 1.0, kd = 1.0, exp = 10.0)
                phong(id = "g", ks = 1.0, cd = Color.GREEN, ka = 1.0, kd = 1.0, exp = 20.0)
                phong(id = "b", ks = 1.0, cd = Color.BLUE, ka = 1.0, kd = 1.0, exp = 20.0)
                reflective(id = "rr", cr = Color.RED, kr = 1.0, ka = 0.0, kd = 0.0, ks = 1.0, cs = Color.RED, exp = 2.0)
                reflective(id = "gr", cr = Color.GREEN, kr = 0.2, ka = 0.0, kd = 0.0, ks = 1.0, cs = Color.GREEN, exp = 5.0)
                reflective(id = "br", cr = Color.BLUE, kr = 0.5, ka = 0.0, kd = 0.0, ks = 1.0, cs = Color.BLUE, exp = 1.2)
            }

            objects {
                plane(material = "grey", point = p(0.0, -1.0, 0.0), normal = n(0.0, 1.0, 0.0))
                plane(material = "sky", point = p(0.0, 99999.0, 0.0), normal = n(0.0, -1.0, 0.0))

                sphere(material = "rr", center = p(1.0, 0.0, 1.0), radius = 0.9)
                sphere(material = "gr", center = p(3.0, 0.0, 1.0), radius = 0.9)
                sphere(material = "br", center = p(5.0, 0.0, 1.0), radius = 0.9)
            }
        }
}
