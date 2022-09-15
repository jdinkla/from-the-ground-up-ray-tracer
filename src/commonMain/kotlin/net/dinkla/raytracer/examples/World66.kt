package net.dinkla.raytracer.examples

import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition

object World66 : WorldDefinition {

    override fun world() = Builder.build("World48") {

        camera(d = 1500.0, eye = p(2.5, 1.35, 10.0), lookAt = p(2.5, 1.0, 0.0))

        ambientLight(ls = 0.5)

        lights {
//            directionalLight(direction = v(-1, -1, -1), ls = 0.5, color = c("FFD700"))
//            directionalLight(direction = v(-1.1, -1.5, -1.1), ls = 0.5, color = c("EEC900"))
            directionalLight(direction = v(-0.9, -0.2, -0.9), ls = 0.5, color = c("ffc0cb"))
        }

        materials {
            matte(id = "gray", cd = c(1.0), ka = 0.25, kd = 0.75)
            reflective(id = "mirror", cd = c("000000"), ka = 0.5, kd = 0.5, ks = 0.2, kr = 0.4, cr = c(1.0, 1.0, 1.0))
            phong(id = "Green Yellow", cd = c("adff2f"), ka = 0.5, kd = 0.75)
            phong(id = "Light Salmon", cd = c("ffa07a"), ka = 0.5, kd = 0.75, ks = 0.55, exp = 15.0)
            phong(id = "Pink", cd = c("ffc0cb"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 9.0)
            phong(id = "Gold1", cd = c("FFD700"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 5.0)
            phong(id = "Gold2", cd = c("EEC900"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 5.0)
        }

        objects {
            plane(material = "mirror")
            sphere(center = p(0, 1, 0), radius = 1.0, material = "Green Yellow")
            sphere(center = p(2, 1, -20), radius = 1.0, material = "Light Salmon")
            sphere(center = p(4, 1, -40), radius = 1.0, material = "Pink")
            sphere(center = p(6, 1, -60), radius = 1.0, material = "Gold1")
            sphere(center = p(8, 1, -80), radius = 1.0, material = "Gold2")
        }
    }
}
