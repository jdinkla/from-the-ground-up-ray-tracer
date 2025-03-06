package net.dinkla.raytracer.examples.materials.transparent

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition

object World71 : WorldDefinition {
    override val id: String = "World71.kt"

    override fun world() =
        Builder.build {
            camera(d = 2000.0, eye = p(-4.0, 3.0, 20.0), lookAt = p(3.0, 1.0, 1.0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(0.0, 10.0, 5.0), ls = 1.0)
            }

            materials {
                matte(id = "sky", cd = c(0.3, 0.6, 1.0), ka = 1.0, kd = 0.9)
                phong(id = "grey", ks = 0.4, cd = c(0.4), ka = 0.25, kd = 0.6, exp = 10.0)
                phong(id = "r", ks = 1.0, cd = Color.RED, ka = 1.0, kd = 1.0, exp = 10.0)
                phong(id = "g", ks = 1.0, cd = Color.GREEN, ka = 1.0, kd = 1.0, exp = 20.0)
                phong(id = "b", ks = 1.0, cd = Color.BLUE, ka = 1.0, kd = 1.0, exp = 20.0)
                reflective(id = "rr", ks = 1.0, cd = Color.RED, ka = 1.0, kd = 1.0, exp = 10.0)
                reflective(id = "gr", ks = 1.0, cd = Color.GREEN, ka = 1.0, kd = 1.0, exp = 20.0)
                reflective(id = "br", ks = 1.0, cd = Color.BLUE, ka = 1.0, kd = 1.0, exp = 20.0)
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
                    ior = 1.0,
                )
                transparent(
                    id = "trans2",
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
                plane(material = "r")
                plane(material = "b", point = p(0.0, 100.0, 0.0), normal = n(0.0, -1.0, 0.0))
                sphere(material = "trans2", center = p(3.0, 1.0, 0.0), radius = 1.0)
                alignedBox(p = p(-1.25, 0.0, -2.25), q = p(-1.0, 2.0, -2.0), material = "rr")
                alignedBox(p = p(7.0, 0.0, -2.25), q = p(7.25, 2.0, -2.0), material = "gr")
                alignedBox(p = p(3.0, 0.0, -2.25), q = p(3.25, 2.0, -2.0), material = "br")
                alignedBox(p = p(-1.25, 0.0, 2.0), q = p(-1.0, 2.0, 2.25), material = "r")
                alignedBox(p = p(7.0, 0.0, 2.0), q = p(7.25, 2.0, 2.25), material = "g")
                alignedBox(p = p(3.0, 0.0, 2.0), q = p(3.25, 2.0, 2.25), material = "b")
                val v = 0.5
                alignedBox(p = p(3.0 - v, 1.0 - v, 0.0 - v), q = p(3.0 + v, 1.0 + v, 0.0 + v), material = "b")
            }
        }
}
