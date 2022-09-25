package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.acceleration.Acceleration
import net.dinkla.raytracer.utilities.Ply
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition

object World74kdt : WorldDefinition {

    override val id: String = "World74kdt.kt"

    override fun world() = Builder.build {

        camera(d = 1000.0, eye = p(0.0, 0.5, 3.0), lookAt = p(0.0, 0.0, 0.0))

        ambientLight(color = Color.WHITE, ls = 0.75)

        lights {
            pointLight(location = p(-1, 2, 3), color = c(1.0, 1.0, 1.0), ls = 1.0)
        }

        materials {
            phong(id = "provenceBlue", cd = c(111, 148, 205), ka = 0.75, kd = 1.0, exp = 10.0)
            phong(id = "driedSage", ks = 0.7, cd = c(185, 187, 136), ka = 0.5, kd = 1.0, exp = 10.0)
            phong(id = "lavender", ks = 0.7, cd = c(148, 131, 185), ka = 0.5, kd = 0.7, exp = 10.0)
            phong(id = "marseilleYellow", ks = 0.7, cd = c(233, 190, 84), ka = 0.5, kd = 0.7, exp = 10.0)
            phong(id = "limonade", ks = 0.7, cd = c(242, 239, 197), ka = 0.5, kd = 0.7, exp = 10.0)
            phong(id = "provenceIndigo", ks = 0.9, cd = c(65, 95, 155), ka = 0.5, kd = 0.7, exp = 10.0)
            phong(id = "freshLinen", ks = 0.9, cd = c(250, 251, 244), ka = 0.5, kd = 0.7, exp = 10.0)
        }

        objects {
            plane(point = p(0, 0, 0), normal = Normal.UP, material = "freshLinen")
            plane(point = p(0, 10, 0), normal = Normal.DOWN, material = "provenceBlue")

            val bunny = Ply.fromFile(
                fileName = "resources/bunny4K.ply",
                isSmooth = true,
                type = Acceleration.KDTREE,
                material = materials["driedSage"]!!
            )

            instance(of = bunny.compound, material = "lavender") {
                scale(v(4.0, 4.0, 4.0))
                rotate(Axis.Y, 10.0)
                translate(v(-1.0, -0.14, 0.0))
            }

            instance(of = bunny.compound, material = "driedSage") {
                scale(v(4.0, 4.0, 4.0))
                rotate(Axis.Y, 10.0)
                translate(v(0.0, -0.14, 0.0))
            }

            instance(of = bunny.compound, material = "limonade") {
                scale(v(4.0, 4.0, 4.0))
                rotate(Axis.Y, 10.0)
                translate(v(1.0, -0.14, 0.0))
            }

            instance(of = bunny.compound, material = "provenceIndigo") {
                scale(v(4.0, 4.0, 4.0))
                rotate(Axis.Y, 10.0)
                translate(v(2.0, -0.14, 0.0))
            }

            instance(of = bunny.compound, material = "marseilleYellow") {
                scale(v(4.0, 4.0, 4.0))
                rotate(Axis.Y, 10.0)
                translate(v(-2.0, -0.14, 0.0))
            }
        }

    }
}
