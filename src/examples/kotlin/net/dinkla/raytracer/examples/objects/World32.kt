package net.dinkla.raytracer.examples.objects

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.SmoothTriangle
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object World32 : WorldDefinition {
    override val id: String = "World32.kt"

    override fun world(): World =
        Builder.build {
            val t1 =
                SmoothTriangle(p(0.75, 1.0, 1.1), p(1.25, 1.25, 0.9), p(1.25, 0.75, 1.0)).apply {
                    n1 = Normal(0.0, 1.1, 1.1)
                    n2 = Normal(1.1, 0.0, -0.5)
                }

            camera(d = 1000.0, eye = p(0, 0, 5), lookAt = p(0, -1, 0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(0.0, 5.0, 0.0), ls = 1.0)
            }

            materials {
                matte(id = "grey", cd = c(0.4), ka = 0.25, kd = 0.8)
                phong(id = "r", ks = 1.0, cd = Color.RED, ka = 1.0, kd = 1.0, exp = 10.0)
                phong(id = "g", ks = 1.0, cd = Color.GREEN, ka = 1.0, kd = 1.0, exp = 20.0)
                phong(id = "b", ks = 1.0, cd = Color.BLUE, ka = 1.0, kd = 1.0, exp = 20.0)
            }

            objects {
                plane(material = "grey", p(0.0, -2.0, 0.0))

                instance(material = "r", of = t1) {
                    translate(v(0.2, -1.0, 0.0))
                }

                instance(material = "g", of = t1) {
                    translate(v(-1, 0, 0))
                    rotate(Axis.Z, 120.0)
                }

                instance(material = "b", of = t1) {
                    rotate(Axis.Z, 240.0)
                }
            }
        }
}
