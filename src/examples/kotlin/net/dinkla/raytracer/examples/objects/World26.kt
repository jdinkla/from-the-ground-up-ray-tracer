package net.dinkla.raytracer.examples.objects

import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

val sph1 = Sphere(radius = 0.25)

object World26 : WorldDefinition {
    override val id: String = "World26.kt"

    override fun world(): World =
        Builder.build {
            camera(d = 250.0, eye = p(0, 2, 10))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(2, 100, 1))
            }

            materials {
                matte(id = "m1", cd = c(1, 1, 0), ka = 0.75, kd = 0.75)
                matte(id = "m2", cd = c(1.0), ka = 0.75, kd = 0.75)
            }

            objects {
                instance(of = sph1, material = "m1") {
                    scale(v(10, 17, 15))
                    translate(v(0, 5, 0))
                    rotate(Axis.X, 12.0)
                    rotate(Axis.Z, 12.0)
                }
                plane(material = "m2", point = Point3D.ORIGIN, normal = Normal.UP)
            }
        }
}
