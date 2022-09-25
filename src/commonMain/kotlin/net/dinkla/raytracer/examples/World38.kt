package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition

object World38 : WorldDefinition {

    override val id: String = "World38.kt"

    override fun world() = Builder.build {

        camera(d = 500.0, eye = p(0, 5, 10), lookAt = p(0, 1, 0))

        ambientLight(color = Color.WHITE, ls = 0.5)

        lights {
            pointLight(location = p(0, 0, 10), ls = 1.0)
        }

        materials {
            matte(id = "sky", cd = c(0.4, 0.7, 1.0), ka = 1.0, kd = 0.9)
            phong(id = "grey", ks = 0.4, cd = c(0.4, 0.4, 0.4), ka = 0.25, kd = 0.6, exp = 10.0)
            phong(id = "r", ks = 1.0, cd = Color.RED, ka = 1.0, kd = 1.0, exp = 10.0)
            phong(id = "g", ks = 1.0, cd = Color.GREEN, ka = 1.0, kd = 1.0, exp = 20.0)
            phong(id = "b", ks = 1.0, cd = Color.BLUE, ka = 1.0, kd = 1.0, exp = 20.0)
            phong(id = "c", ks = 1.0, cd = c(0.0, 1.0, 1.0), ka = 1.0, kd = 1.0, exp = 10.0)
            phong(id = "y", ks = 1.0, cd = c(1.0, 1.0, 0.0), ka = 1.0, kd = 1.0, exp = 10.0)
        }

        objects {
            grid {
                sphere(center = p(-1, -1, 0), radius = 0.25, material = "r")
            }

            grid {
                sphere(center = p(1, 1, 0), radius = 0.25, material = "r")
                sphere(center = p(2, 1, 0), radius = 0.25, material = "g")
                sphere(center = p(3, 1, 0), radius = 0.25, material = "b")
            }

            grid {
                grid {
                    sphere(center = p(2, 2, 0), radius = 0.25, material = "c")
                }
                grid {
                    sphere(center = p(4, 4, 0), radius = 0.25, material = "y")
                }
            }
        }
    }
}