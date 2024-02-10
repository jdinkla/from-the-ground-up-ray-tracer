package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition

object TransparentSpheres : WorldDefinition {

    override val id: String = "TransparentSpheres.kt"

    override fun world() = Builder.build {
        camera(d = 1500.0, eye = p(-4.0, -0.25, 4.0), lookAt = p(3.0, -0.2, 1.0), up = v(0, 1, 0))

        ambientLight(ls = 0.7)

        lights {
            pointLight(location = p(0, 0, 5), ls = 1.0)
        }

        materials {
            matte(id = "sky", cd = c(0.3, 0.6, 1.0), ka = 1.0, kd = 0.9)
            phong(id = "grey", ks = 0.4, cd = c(0.4), ka = 0.25, kd = 0.6, exp = 10.0)
            reflective(id = "rr", ks = 1.0, cd = Color.RED, ka = 1.0, kd = 1.0, exp = 10.0)
            reflective(id = "gr", ks = 1.0, cd = Color.GREEN, ka = 1.0, kd = 1.0, exp = 20.0)
            reflective(id = "br", ks = 1.0, cd = Color.BLUE, ka = 1.0, kd = 1.0, exp = 20.0)
            transparent(
                id = "rt",
                ks = 0.1,
                ka = 0.1,
                kd = 0.2,
                kt = 0.5,
                kr = 0.1,
                exp = 10.0,
                ior = 1.02,
                cd = Color.RED,
            )
            transparent(
                id = "gt",
                ks = 0.1,
                ka = 0.1,
                kd = 0.2,
                kt = 0.5,
                kr = 0.1,
                exp = 10.0,
                ior = 1.02,
                cd = Color.GREEN,
            )
            transparent(
                id = "bt",
                ks = 0.1,
                ka = 0.1,
                kd = 0.2,
                kt = 0.5,
                kr = 0.1,
                exp = 10.0,
                ior = 1.02,
                cd = Color.BLUE,
            )
        }

        objects {
            plane(material = "grey", point = p(0, -1, 0), normal = n(0, 1, 0))
            plane(material = "sky", point = p(0, 10000, 0), normal = n(0, -1, 0))
            sphere(material = "rt", center = p(1, 0, 1), radius = 0.9)
            sphere(material = "gt", center = p(3, 0, 1), radius = 0.9)
            sphere(material = "bt", center = p(5, 0, 1), radius = 0.9)
        }
    }
}
