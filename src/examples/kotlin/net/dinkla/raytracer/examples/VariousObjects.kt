package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition

object VariousObjects : WorldDefinition {
    override val id: String = "VariousObjects.kt"
    override fun world() = Builder.build {
        camera(d = 2000.0, eye = p(0, 100, 200), lookAt = p(0, 0, 0))

        ambientLight(color = Color.WHITE, ls = 1.0)

        lights {
            pointLight(location = p(100, 50, 150), ls = 3.14)
            pointLight(location = p(-100, 50, -30), ls = 1.6, color = c(0.9, 0.0, 0.0))
            pointLight(location = p(200, 180, 10), ls = 2.6, color = c(0.0, 0.9, 1.0))
        }

        materials {
            phong(id = "m1", cd = c(1.0, 1.0, 0.0), ka = 0.25, kd = 0.65)
            phong(id = "m2", cd = c(0.71, 0.40, 0.16), ka = 0.25, kd = 0.65)
            matte(id = "m3", cd = c(0.9, 0.9, 0.9), ka = 0.25, kd = 0.65)
            matte(id = "m4", cd = c(0.2, 0.4, 0.7), ka = 0.25, kd = 0.65)
            matte(id = "m5", cd = c(0.4, 0.7, 0.2), ka = 0.25, kd = 0.65)
            matte(id = "m6", cd = c(0.8, 0.0, 0.61), ka = 0.25, kd = 0.65)
        }

        objects {
            sphere(material = "m1", center = p(10, -5, 0), radius = 27.0)
            sphere(material = "m2", center = p(-30, 15, -50), radius = 27.0)
            plane(material = "m3", point = Point3D.ORIGIN, normal = Normal.UP)
            triangle(material = "m4", a = p(-30, 0, 0), b = p(0, 30, 0), c = p(-30, 20, 10), smooth = true)
            disk(material = "m5", center = p(-50, 10, 0), radius = 15.0, normal = n(1, 1, -1))
            rectangle(material = "m6", p0 = p(20, 20, 60), a = v(5, 0, 0), b = v(0, 10, 0))
        }
    }
}
