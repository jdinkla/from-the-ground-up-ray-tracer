package net.dinkla.raytracer.examples.materials.transparent

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object World71b : WorldDefinition {
    override val id: String = "World71b.kt"
    override fun world(): World = Builder.build {
        camera(d = 3000.0, eye = p(-4, 3, 20), lookAt = p(3, 1, 1))

        ambientLight(ls = 0.5)

        lights {
            pointLight(location = p(0, 10, 5), ls = 1.0)
        }

        materials {
            matte(id = "sky", cd = c(0.3, 0.6, 1.0), ka = 1.0, kd = 0.9)
            phong(id = "grey", ks = 0.4, cd = c(0.4), ka = 0.25, kd = 0.6, exp = 10.0)
            phong(id = "r", ks = 1.0, cd = Color.RED, ka = 1.0, kd = 1.0, exp = 10.0)
            phong(id = "g", ks = 1.0, cd = Color.GREEN, ka = 1.0, kd = 1.0, exp = 20.0)
            phong(id = "b", ks = 1.0, cd = Color.BLUE, ka = 1.0, kd = 1.0, exp = 20.0)
            transparent(
                id = "trans",
                ks = 1.0,
                ka = 0.0,
                kd = 1.0,
                kt = 0.5,
                kr = 0.5,
                exp = 10.0,
                ior = 1.01,
            )
        }

        objects {
            plane(material = "grey")
            plane(material = "sky", point = p(0, 100, 0), normal = n(0, -1, 0))
            sphere(material = "trans", center = p(3, 1, 0), radius = 1.0)
            alignedBox(p = p(-1.25, 0.0, -2.25), q = p(-1.0, 2.0, -2.0), material = "r")
            alignedBox(p = p(7.0, 0.0, -2.25), q = p(7.25, 2.0, -2.0), material = "g")
            alignedBox(p = p(3.0, 0.0, -2.25), q = p(3.25, 2.0, -2.0), material = "b")
            alignedBox(p = p(-1.25, 0.0, 2.0), q = p(-1.0, 2.0, 2.25), material = "r")
            alignedBox(p = p(7.0, 0.0, 2.0), q = p(7.25, 2.0, 2.25), material = "g")
            alignedBox(p = p(3.0, 0.0, 2.0), q = p(3.25, 2.0, 2.25), material = "b")
            val v = 0.5
            alignedBox(p = p(3.0 - v, 1.0 - v, 0.0 - v), q = p(3.0 + v, 1.0 + v, 0.0 + v), material = "b")
        }
    }
}
