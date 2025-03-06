package net.dinkla.raytracer.examples

import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition

object TwoSpheresSinkIntoPlane : WorldDefinition {
    override val id: String = "TwoSpheresSinkIntoPlane.kt"

    override fun world() =
        Builder.build {
            metadata {
                id("World6")
                description("reflective")
            }

            camera(d = 750.0, eye = p(0, 30, 80), lookAt = p(0, 10, 0))

            ambientLight(ls = 0.75)

            lights {
                pointLight(location = p(100, 50, 150))
            }

            materials {
                reflective(id = "m1", cd = c(1.0, 1.0, 0.0), ka = 0.25, kd = 0.65)
                reflective(id = "m2", cd = c(0.71, 0.40, 0.16), ka = 0.25, kd = 0.65)
                reflective(id = "m3", cd = c(0.7), ka = 0.25, kd = 0.65)
            }

            objects {
                sphere(material = "m1", center = p(10, -5, 0), radius = 27.0)
                sphere(material = "m2", center = p(-30, 15, -50), radius = 27.0)
                plane(material = "m3")
            }
        }
}
