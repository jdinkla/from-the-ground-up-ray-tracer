package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.objects.acceleration.kdtree.builder.Simple2Builder
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDef

object World75b : WorldDef {

    const val NUM = 20
    const val NUM2 = NUM / 2

    override fun world() = Builder.build("World75") {

        // viewPlane(resolution= Resolution.RESOLUTION_1440, maxDepth= 2, numSamples= 4)

        camera(d = 1000.0, eye = p(0, 0, 5), lookAt = p(0, 0, 0))

        ambientLight(color = Color.WHITE, ls = 0.5)

        lights {
            pointLight(location = p(-1, 2, 3), color = c(1.0, 1.0, 1.0), ls = 1.0)
        }

        materials {
            phong(id = "grey", ks = 1.0, cd = c(0.1, 0.1, 0.1), ka = 0.5, kd = 1.0, exp = 10.0)
            matte(id = "sky", cd = c(0.1, 0.7, 1.0), ka = 0.75, kd = 1.0)
            reflective(id = "white", ks = 0.7, cd = c(1.0, 1.0, 1.0), ka = 0.5, kd = 0.75, exp = 2.0)
            reflective(id = "p0", ks = 0.7, cd = c(1.00, 0.86, 0.57), ka = 0.2, kd = 0.7, exp = 2.0)
            reflective(id = "p1", ks = 0.7, cd = c(0.98, 0.97, 0.95), ka = 0.3, kd = 0.7, exp = 2.0)
            reflective(id = "p2", ks = 0.7, cd = c(0.98, 0.82, 0.76), ka = 0.4, kd = 0.7, exp = 2.0)
            reflective(id = "p3", ks = 0.9, cd = c(1.0, 0.95, 0.25), ka = 0.5, kd = 0.7, exp = 10.0)
        }

        objects {

            //plane(point= p(0,0,0), normal= n(0, 1, 0), material= "white")
            kdtree(builder = Simple2Builder()) {
                for (k in 0 until NUM) {
                    for (j in 0 until NUM) {
                        for (i in 0 until NUM) {
                            val pc = (i + j + k) % 4
                            sphere(center = p(-NUM2 + i, -NUM2 + j, -k), radius = 0.25, material = "p${pc}")
                        }
                    }
                }
            }
        }

    }
}
