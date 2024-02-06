package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.Sphere
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition

object World72 : WorldDefinition {

    override val id: String = "World72.kt"

    // val tex1 = ImageTexture("/opt/rendering/textures/rtftgu/SphereGrid.png")

    override fun world() = Builder.build {
        // tex1.mapping = SphericalMap()
        val sphere = Sphere(center = p(0, 1, 0), radius = 1.0)

        //  camera(d: 2000, eye: p(-4, 3, 20), lookAt: p(3, 1, 1), numThreads: 30)
        camera(d = 750.0, eye = p(0.0, 1.2, 10.0), lookAt = p(0.0, 0.8, 0.0))

        ambientLight(ls = 0.5)

        lights {
            pointLight(location = p(0.0, 10.0, 5.0), ls = 1.0)
        }

        materials {
            matte(id = "sky", cd = c(0.3, 0.6, 1.0), ka = 1.0, kd = 0.9)
            phong(id = "grey", ks = 0.4, cd = c(0.4), ka = 0.25, kd = 0.6, exp = 10.0)
            phong(id = "r", ks = 1.0, cd = Color.RED, ka = 1.0, kd = 1.0, exp = 10.0)
            phong(id = "g", ks = 1.0, cd = Color.GREEN, ka = 1.0, kd = 1.0, exp = 20.0)
            phong(id = "b", ks = 1.0, cd = Color.BLUE, ka = 1.0, kd = 1.0, exp = 20.0)
        }

        objects {
            plane(material = "grey")
            plane(material = "sky", point = p(0.0, 100.0, 0.0), normal = Normal.DOWN)

            sphere(material = "id", center = p(0, 1, 0), radius = 0.5)
//            instance(material = "tex1", theObject = sphere) {
//                translate(v(0, 0, 0))
//            }
        }
    }
}
