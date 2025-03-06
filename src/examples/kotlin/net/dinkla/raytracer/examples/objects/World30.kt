package net.dinkla.raytracer.examples.objects

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.AlignedBox
import net.dinkla.raytracer.objects.compound.SolidCylinder
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object World30 : WorldDefinition {
    override val id: String = "World30.kt"

    override fun world(): World =
        Builder.build {
            val b1 = AlignedBox(p = p(0, 0, 0), q = p(5.0, 1.75, 0.15))
            val s1 = SolidCylinder(y0 = 0.0, y1 = 2.5, radius = 0.5)

            camera(d = 900.0, eye = p(0, 1, -6), lookAt = p(-0.5, 1.25, 0.0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(10, 7, 0), ls = 1.0)
            }

            materials {
                matte(id = "m1", cd = c(1, 1, 1), ka = 0.25, kd = 0.9)
                matte(id = "m2", cd = c(0.1, 0.7, 0.3), ka = 0.25, kd = 0.75)
                matte(id = "m3", cd = c(1, 1, 0), ka = 0.25, kd = 0.75)
                matte(id = "m4", cd = c(1, 0, 0), ka = 0.25, kd = 0.75)
                matte(id = "m5", cd = c(0.0, 0.5, 1.0), ka = 0.25, kd = 0.75)
            }

            objects {
                plane(material = "m1", point = Point3D.ORIGIN, normal = Normal.UP)
                instance(material = "m2", of = b1) {
                    rotate(Axis.Y, -20.0)
                    translate(v(-3.0, 0.0, 0.0))
                }
                sphere(material = "m3", center = p(0.5, 0.6, -1.0), radius = 0.6)
                sphere(material = "m4", center = p(-1.5, 0.4, 0.5), radius = 0.4)
                instance(material = "m5", of = s1) {
                    translate(v(-0.7, 0.0, -1.0))
                }
            }
        }
}
