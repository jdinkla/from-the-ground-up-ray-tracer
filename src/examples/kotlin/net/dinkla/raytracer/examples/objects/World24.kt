package net.dinkla.raytracer.examples.objects

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object World24 : WorldDefinition {
    override val id: String = "World24.kt"

    override fun world(): World =
        Builder.build {
            camera(d = 1000.0, lookAt = p(2, 0, 0), eye = p(0, 1, 5))

            ambientLight(ls = 0.0)

            lights {
                pointLight(location = p(2, 5, 10), ls = 2.0)
            }

            materials {
                matte(id = "m1", cd = c(1.0, 0.7, 0.0), ka = 0.75, kd = 0.75)
                matte(id = "m2", cd = c(1.0), ka = 0.75, kd = 0.75)
                matte(id = "m3", cd = c(0.2, 0.5, 0.4), ka = 0.25, kd = 0.5)
            }

            objects {
                plane(material = "m2", point = Point3D.ORIGIN, normal = Normal.UP)
                solidCylinder(material = "m1", y0 = 0.0, y1 = 1.5, radius = 0.9)
                alignedBox(material = "m3", p = p(4, 0, 0), q = p(5.0, 1.5, 1.0))
            }
        }
}
