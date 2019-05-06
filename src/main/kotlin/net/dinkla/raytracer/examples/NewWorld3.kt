package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDef

object NewWorld3 : WorldDef {

    override fun world(): World = Builder.build("New World 3 - emissive") {

        camera(d = 1000.0, eye = p(0, 0, 0), lookAt = p(0, 0, 1000))

        ambientLight(ls = 0.5)

        lights {
            directionalLight(direction = Vector3D.FORWARD, ls = 2.0, color = Color.WHITE)
        }

        materials {
            emissive(id = "tangerine yellow matte", ce = c("FFCC00"), le = 10.0)
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