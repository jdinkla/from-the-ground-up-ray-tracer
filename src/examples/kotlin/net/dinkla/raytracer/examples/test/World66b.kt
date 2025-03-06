package net.dinkla.raytracer.examples.test

import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition

object World66b : WorldDefinition {
    override val id: String = "World66b.kt"

    override fun world() =
        Builder.build {
            camera(d = 1000.0, eye = p(2.5, 1.35, 10.0), lookAt = p(2.5, 1.0, 0.0))

            ambientLight(ls = 0.0)

            lights {
                directionalLight(direction = v(-1, -1, -1), ls = 0.5, color = c("FFD700"))
                directionalLight(direction = v(-1.1, -1.5, -1.1), ls = 0.5, color = c("EEC900"))
                directionalLight(direction = v(-0.9, -0.2, -0.9), ls = 1.0, color = c("ffc0cb"))
            }

            materials {
                matte(id = "gray", cd = c(1.0), ka = 0.25, kd = 0.75)
                reflective(id = "mirror", cd = c("0000FF"), ka = 0.0, kd = 1.0, ks = 1.0, kr = 1.0, cr = c(1.0, 0.0, 1.0))
                phong(id = "Green Yellow", cd = c("adff2f"), ka = 0.5, kd = 0.75, ks = 0.05, exp = 20.0)
                phong(id = "Light Salmon", cd = c("ffa07a"), ka = 0.5, kd = 0.75, ks = 0.55, exp = 15.0)
                phong(id = "Pink", cd = c("ffc0cb"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 9.0)
                phong(id = "Gold1", cd = c("FFD700"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 5.0)
                phong(id = "Gold2", cd = c("EEC900"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 5.0)
            }

            objects {
                plane(material = "gray")
                sphere(center = p(0, 1, 0), radius = 1.0, material = "Green Yellow")
                sphere(center = p(2, 1, -20), radius = 1.0, material = "Green Yellow")
                sphere(center = p(4, 1, -40), radius = 1.0, material = "Green Yellow")
                sphere(center = p(6, 1, -60), radius = 1.0, material = "Green Yellow")
                sphere(center = p(8, 1, -80), radius = 1.0, material = "Green Yellow")
            }
        }
}
