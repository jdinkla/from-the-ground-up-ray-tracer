package net.dinkla.raytracer.examples.test

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.Rectangle
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition
import kotlin.math.sqrt

object World28 : WorldDefinition {
    override val id: String = "World28.kt"
    override fun world(): World = Builder.build {
        val sqrt200 = sqrt(200.0)
        val r1 = Rectangle(p0 = Point3D.ORIGIN, a = v(sqrt200, 0.0, 0.0), b = v(0, 10, 0))

        camera(d = 1500.0, eye = p(0, 10, 20))

        ambientLight(ls = 0.8)

        lights {
            pointLight(location = p(0.0, 9.0, 0.5), ls = 1.0)
        }

        materials {
            matte(id = "red", cd = c(1, 0, 0), ka = 1.0, kd = 1.0)
            matte(id = "m1", cd = c(1, 1, 1), ka = 0.25, kd = 0.9)

            phong(id = "p1", ks = 0.5, cd = c(1, 1, 0), ka = 0.25, kd = 0.75, exp = 50.0)
            phong(id = "p2", ks = 0.5, cd = c(0, 1, 1), ka = 0.25, kd = 0.75, exp = 50.0)
            phong(id = "p3", ks = 0.5, cd = c(1, 0, 1), ka = 0.25, kd = 0.75, exp = 50.0)
            phong(id = "p4", ks = 0.5, cd = c(1.0, 0.75, 0.75), ka = 0.25, kd = 0.75, exp = 50.0)

            reflective(id = "mX", ks = 1.0, cd = c(1, 1, 1), ka = 0.25, kd = 0.9, exp = 1.0)
            reflective(id = "m2", ks = 0.5, cd = c(0.1, 0.7, 0.3), ka = 0.25, kd = 0.75, exp = 10.0)
            reflective(id = "m3", ks = 0.5, cd = c(1, 1, 0), ka = 0.25, kd = 0.75, exp = 50.0)
            reflective(id = "m4", ks = 0.1, cd = c(1, 0, 0), ka = 0.25, kd = 0.75, exp = 3.0)
            reflective(id = "m5", ks = 0.5, cd = c(0.0, 0.5, 1.0), ka = 0.25, kd = 0.75, exp = 10.0)
        }

        objects {
            rectangle(p0 = Point3D.ORIGIN, a = v(10, 0, 10), b = v(-10, 0, 10), material = "m1")

            sphere(material = "red", center = p(0, 0, 0), radius = 1.0)
            sphere(material = "m1", center = p(0, 4, 10), radius = 1.0)

            instance(material = "p1", of = r1) {
                rotate(Axis.Y, 45.0)
            }

            instance(material = "p2", of = r1) {
                translate(v(-sqrt200, 0.0, 0.0))
                rotate(Axis.Y, -45.0)
            }

            instance(material = "p3", of = r1) {
                rotate(Axis.Y, 45.0)
                translate(v(-10, 0, 10))
            }

            instance(material = "p4", of = r1) {
                translate(v(-sqrt200, 0.0, 0.0))
                rotate(Axis.Y, -45.0)
                translate(v(10, 0, 10))
            }

        }
    }
}

