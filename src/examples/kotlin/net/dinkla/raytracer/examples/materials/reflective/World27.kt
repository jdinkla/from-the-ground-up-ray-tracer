package net.dinkla.raytracer.examples.materials.reflective

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.AlignedBox
import net.dinkla.raytracer.objects.compound.SolidCylinder
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object World27 : WorldDefinition {
    override val id: String = "World27.kt"

    override fun world(): World =
        Builder.build {
            val b1 = AlignedBox(p = p(0, 0, 0), q = p(5.0, 1.75, 0.15))
            val s1 = SolidCylinder(y0 = 0.0, y1 = 2.5, radius = 0.5)

            ambientLight(ls = 0.55)

            camera(d = 1750.0, eye = p(0.0, 3.2, -10.0), lookAt = p(-0.5, 1.2, 0.0))

            lights {
                pointLight(location = p(10, 7, 0), ls = 1.0)
            }

            materials {
                reflective(id = "m1", ks = 1.0, cd = c(1, 1, 1), ka = 0.25, kd = 0.9, exp = 1.0)
                reflective(id = "m2", ks = 0.5, cd = c(0.1, 0.7, 0.3), ka = 0.25, kd = 0.75, exp = 10.0)
                reflective(id = "m3", ks = 0.5, cd = c(1, 1, 0), ka = 0.25, kd = 0.75, exp = 50.0)
                reflective(id = "m4", ks = 0.1, cd = c(1, 0, 0), ka = 0.25, kd = 0.75, exp = 3.0)
                reflective(id = "m5", ks = 0.5, cd = c(0.0, 0.5, 1.0), ka = 0.25, kd = 0.75, exp = 10.0)
                matte(id = "black", cd = c(0, 0, 0))
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
