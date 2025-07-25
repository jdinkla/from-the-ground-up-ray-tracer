package net.dinkla.raytracer.examples

import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object YellowAndOrangeSphere : WorldDefinition {
    override val id: String = "YellowAndOrangeSphere.kt"

    override fun world(): World =
        Builder.build {
            metadata {
                id("World 10")
                title("World 10")
                description("This should describe the world.")
            }

            camera(d = 8000.0, eye = p(0, 0, 500), lookAt = p(0, 0, 0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(100, 50, 150), ls = 3.141)
                pointLight(location = p(-100, 50, -30), ls = 1.641, color = c(0.9, 0.0, 0.0))
                pointLight(location = p(400, 180, -200), ls = 2.641, color = c(0.0, 0.9, 1.0))
            }

            materials {
                phong(id = "m1", cd = c(1.0, 1.0, 0.0), ka = 0.25, kd = 0.65, exp = 25.0, ks = 1.0)
                phong(id = "m2", cd = c(0.71, 0.40, 0.16), ka = 0.25, kd = 0.65, exp = 1.0, ks = 0.1)
                phong(id = "m3", cd = c(0.5, 0.5, 0.5), ka = 0.25, kd = 0.55, exp = 15.0, ks = 0.9)
                matte(id = "m4", cd = c(0.5, 0.5, 0.99), ka = 0.1, kd = 0.5)
            }

            objects {
                sphere(center = p(10, -5, 0), radius = 27.0, material = "m1")
                sphere(center = p(-30, 15, -50), radius = 27.0, material = "m2")
                plane(point = p(0, -100, 0), material = "m3")
                plane(point = p(0, 100, 0), normal = n(0, -1, 0), material = "m4")
            }
        }
}
