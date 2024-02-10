package net.dinkla.raytracer.examples

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.compound.SolidCylinder
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object VariousObjects2 : WorldDefinition {
    override val id: String = "VariousObjects2.kt"
    override fun world(): World = Builder.build {

        val sc = SolidCylinder(y0 = 0.0, y1 = 12.5, radius = 0.75)

        camera(d = 750.0, eye = p(0, 1, 10), lookAt = p(2.0, 0.75, 0.0))

        ambientLight(ls = 0.0)

        lights {
            pointLight(location = p(51, 200, 0), ls = 1.0)
            pointLight(location = p(100, 200, 100), ls = 1.0)
            pointLight(location = p(-100, 200, 100), ls = 1.0)
        }

        materials {
            phong(id = "sky", cd = c("1E2D5B"), ka = 1.0, kd = 1.0, ks = 1.0, exp = 4.0)
            reflective(
                id = "yellow_r", cr = c("F7B685"), kr = 0.7, ka = 0.5, kd = 0.5, ks = 0.25, cs = c("F7B685"), exp = 4.0
            )
            reflective(
                id = "green_r", cr = c("6CC54F"), kr = 0.5, ka = 0.5, kd = 0.5, ks = 0.25, cs = c("6CC54F"), exp = 1.0
            )
            reflective(
                id = "red_r", cr = c("AF0A14"), kr = 0.5, ka = 0.5, kd = 0.5, ks = 0.25, cs = c("AF0A14"), exp = 1.0
            )
            reflective(
                id = "blue_r", cr = c("4F6CC5"), kr = 0.5, ka = 0.5, kd = 0.5, ks = 0.25, cs = c("4F6CC5"), exp = 1.0
            )
            reflective(id = "gray_r", cr = c(0.1), kr = 0.7, ka = 0.5, kd = 0.5, ks = 0.25, cs = c(1.0))
            reflective(
                id = "purple_r", cr = c("9E8AD9"), kr = 0.7, ka = 0.5, kd = 0.5, ks = 0.25, cs = c("9E8AD9"), exp = 2.0
            )
            reflective(
                id = "turq_r", cr = c("1E4C5C"), kr = 0.8, ka = 0.5, kd = 0.5, ks = 0.25, cs = c("1E4C5C"), exp = 2.0
            )
        }

        objects {
            plane(material = "gray_r", point = Point3D.ORIGIN, normal = Normal.UP)
            plane(material = "sky", point = p(0, 1000, 0), normal = Normal.DOWN)
            sphere(material = "blue_r", center = p(0, 2, 0), radius = 2.0)
            sphere(material = "red_r", center = p(1.0, 0.75, 4.0), radius = 0.75)
            beveledBox(p0 = p(1, 0, 6), p1 = p(2.0, 1.6, 7.0), rb = 0.1, material = "purple_r", isWiredFrame = true)
            alignedBox(material = "green_r", p = p(4, 0, 1), q = p(4 + 0.25, 0.0 + 3, 1.0 + 5))
            alignedBox(material = "yellow_r", p = p(-1.5, 0.0, 6.0), q = p(-1.5 + 0.5, 0.0 + 0.5, 6.0 + 0.5))
            instance(of = sc, material = "turq_r") {
                translate(v(-3, 0, 3))
            }
        }
    }
}
