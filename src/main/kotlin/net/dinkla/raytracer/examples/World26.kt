package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition

object World26 : WorldDefinition {

    override fun world() = Builder.build("World 26") {

        camera(d = 2000.0, eye = p(0, 1, -10), lookAt = p(0, 1, 0))

        ambientLight(ls = 0.5)

        lights {
            pointLight(location = p(0, 10, -10), ls = 2.0)
        }

        materials {
            matte(id = "yellow", cd = c(1.0, 1.0, 0.0), ka = 0.75, kd = 0.75)
            matte(id = "grey", cd = c(0.7), ka = 0.75, kd = 0.75)
            matte(id = "red", cd = Color.RED)
        }

        val sphere = Sphere(radius = 1.0)

        objects {
            plane(material = "grey", normal = Normal.UP)
            instance(of = sphere, material = "red") {
                translate(v(-2.0, 1.0, 0.0))
            }
            instance(of = sphere, material = "yellow") {
                scale(v(1.5, 1.1, 0.6))
                translate(v(2.0, 1.1, 0.0))
                rotateX(12.0)
                rotateZ(12.0)
            }
        }
    }
}