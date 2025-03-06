package net.dinkla.raytracer.examples.acceleration

import net.dinkla.raytracer.objects.acceleration.kdtree.builder.Simple2Builder
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition
import net.dinkla.raytracer.world.repeat3

const val NUM = 3
const val NUM2 = NUM / 2

object World75 : WorldDefinition {
    override val id: String = "World75.kt"

    override fun world(): World =
        Builder.build {
            metadata {
                description = "Does not work"
            }

            camera(d = 1000.0, eye = p(0, 0, 5), lookAt = p(0, 0, 0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(-1, 2, 3), ls = 1.0)
                pointLight(location = p(0, 0, 1), ls = 1.0)
                pointLight(location = p(0, 1, 1), ls = 1.0)
            }

            materials {
                phong(id = "p0", ks = 0.7, cd = c(0.95, 0.2, 0.05), ka = 0.5, kd = 0.7, exp = 10.0)
                phong(id = "p1", ks = 0.7, cd = c(0.2, 0.95, 0.05), ka = 0.5, kd = 0.7, exp = 20.0)
                phong(id = "p2", ks = 0.7, cd = c(0.2, 0.05, 0.95), ka = 0.5, kd = 0.7, exp = 30.0)
                phong(id = "p3", ks = 0.9, cd = c(1.0, 0.95, 0.25), ka = 0.5, kd = 0.7, exp = 10.0)
            }

            objects {
                kdtree(builder = Simple2Builder()) {
                    repeat3(NUM) { i, j, k ->
                        sphere(center = p(-NUM2 + i, -NUM2 + j, -k), radius = 0.25, material = "p${(i + j + k) % 4}")
                    }
                }
            }
        }
}
