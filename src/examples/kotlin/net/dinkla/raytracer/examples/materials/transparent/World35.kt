package net.dinkla.raytracer.examples.materials.transparent

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object World35 : WorldDefinition {
    override val id: String = "World35.kt"
    override fun world(): World = Builder.build {
        val s = Sphere(radius = 1.0)
        camera(d = 3000.0, eye = p(-4, 1, 20), lookAt = p(3.0, 1.0, 1.0))

        ambientLight(color = Color.WHITE, ls = 0.5)

        lights {
            pointLight(location = p(0, 0, 5), ls = 1.0)
        }

        materials {
            matte(id = "sky", cd = c(0.3, 0.6, 1.0), ka = 1.0, kd = 0.9)
            phong(
                id = "grey",
                ks = 0.4,
                cd = c(0.4),
                ka = 0.25,
                kd = 0.6,
                exp = 10.0
            )
            phong(id = "red", ks = 1.0, cd = Color.RED, ka = 1.0, kd = 1.0, exp = 10.0)
            transparent(
                id = "rt",
                ks = 0.1,
                ka = 0.1,
                kd = 0.2,
                kt = 0.5,
                kr = 0.5,
                exp = 10.0,
                ior = 1.02,
                cd = Color.RED,
            )
            transparent(
                id = "gt",
                ks = 0.1,
                ka = 0.1,
                kd = 0.4,
                kt = 0.5,
                kr = 0.5,
                exp = 10.0,
                ior = 1.02,
                cd = Color.GREEN,
            )
            transparent(
                id = "bt",
                ks = 0.1,
                ka = 0.1,
                kd = 0.4,
                kt = 0.5,
                kr = 0.5,
                exp = 10.0,
                ior = 1.02,
                cd = Color.BLUE,
            )
            transparent(
                id = "trans",
                ks = 0.0,
                ka = 0.0,
                kd = 0.0,
                kt = 0.5,
                kr = 0.5,
                exp = 10.0,
                ior = 1.5,
            )
        }

        objects {
            plane(material = "grey", point = p(0, -1, 0), normal = n(0, 1, 0))
            plane(material = "sky", point = p(0, 100, 0), normal = n(0, -1, 0))
            box(material = "red", p(-1.0, 0.0, -3.0), a = v(10.0, 0.0, 0.0), b = v(0.0, 3.0, 0.0))
            sphere(material = "rt", center = p(1, 0, 1), radius = 0.9)
            sphere(material = "gt", center = p(3, 0, 1), radius = 0.9)
            sphere(material = "bt", center = p(5, 0, 1), radius = 0.9)
            instance(material = "trans", of = s) {
                scale(v(2.0, 1.0, 1.0))
                translate(v(3.0, 2.75, 0.0))
            }
        }
    }
}
