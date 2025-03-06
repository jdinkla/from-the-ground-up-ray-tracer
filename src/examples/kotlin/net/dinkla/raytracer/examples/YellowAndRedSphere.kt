package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.world.Builder.build
import net.dinkla.raytracer.world.WorldDefinition

object YellowAndRedSphere : WorldDefinition {
    override val id: String = "YellowAndRedSphere.kt"

    override fun world() =
        build {
            camera(d = 1500.0, eye = p(2.0, 0.5, 5.0), lookAt = p(1.5, 1.0, 0.0))

            ambientLight(color = Color.WHITE, ls = 0.25)

            lights {
                pointLight(location = Point3D(2.0, 2.0, 5.0), ls = 2.0)
            }

            materials {
                matte(id = "m1", ka = 0.75, kd = 0.75, cd = c(1.0, 1.0, 0.0))
                matte(id = "m2", ka = 0.75, kd = 0.75, cd = c(1.0))
                phong(id = "m3", ka = 0.25, kd = 0.55, cd = c(1.0, 0.0, 0.0), exp = 10.0, ks = 0.9, cs = Color.WHITE)
            }

            objects {
                sphere(material = "m1", center = p(0, 1, 0), radius = 1.0)
                sphere(material = "m3", center = p(3, 1, 0), radius = 1.0)
                plane(material = "m2", point = Point3D.ORIGIN, normal = Normal.UP)
                plane(material = "m2", point = p(0, 20, 0), normal = Normal.DOWN)
            }
        }
}
