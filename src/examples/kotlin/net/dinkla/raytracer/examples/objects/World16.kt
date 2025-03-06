package net.dinkla.raytracer.examples.objects

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object World16 : WorldDefinition {
    override val id: String = "World16.kt"

    override fun world(): World =
        Builder.build {
            // camera(d = 1000.0, eye = p(0, 0, 4), lookAt = p(0, 0, 0))
            camera(d = 500.0, eye = p(0, 100, 200), lookAt = p(0, 0, 0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(2, 2, 1), ls = 1.0)
            }

            materials {
                matte(id = "m1", cd = c(1, 1, 0), ka = 0.75, kd = 0.75)
                matte(id = "m2", cd = c(1, 1, 1), ka = 0.75, kd = 0.75)
            }

            objects {
                openCylinder(material = "m1", y0 = 0.2, y1 = 1.5, radius = 0.9)
                plane(material = "m2", point = Point3D.ORIGIN, normal = Normal.UP)
            }
        }
}
