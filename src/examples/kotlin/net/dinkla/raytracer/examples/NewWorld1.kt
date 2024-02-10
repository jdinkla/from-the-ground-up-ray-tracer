package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object NewWorld1 : WorldDefinition {
    override val id: String = "NewWorld1.kt"
    override fun world(): World = Builder.build {
        metadata {
            id(id)
        }

        camera(d = 1000.0, eye = p(0, 0, 0), lookAt = p(0, 0, 1000))

        ambientLight(ls = 0.5)

        lights {
            pointLight(location = p(0, 0, 0), ls = 2.0, color = Color.WHITE)
        }

        materials {
            matte(id = "tangerine yellow matte", cd = c("FFCC00"), ka = 0.25, kd = 0.65)
        }

        objects {
            for (i in -3..3) {
                for (j in -1..1) {
                    sphere(center = p(i * 25, j * 25, 100), radius = 10.0, material = "tangerine yellow matte")
                }
            }
        }
    }
}