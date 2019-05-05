package net.dinkla.raytracer.examples

import net.dinkla.raytracer.cameras.render.Renderers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDef

object World6 : WorldDef {

    override fun world() = Builder.build("World6") {

        // // TODO ? viewPlane and Threads, oversampling
//        viewPlane {
//                resolution = Resolution.RESOLUTION_720
//        //, numSamples: 4
//        }

        // camera(d = 750.0, eye = p(0, 30, 80), lookAt = p(0, 10, 0), numThreads = 4)
        camera(d = 750.0, eye = p(0, 30, 80), lookAt = p(0, 10, 0), engine = Renderers.COROUTINE)

        ambientLight(ls = 0.75)

        lights {
            pointLight(location = p(100, 50, 150))
        }

        materials {
            reflective(id = "m1", cd= c(1.0, 1.0, 0.0), ka= 0.25, kd= 0.65)
            reflective(id = "m2", cd= c(0.71, 0.40, 0.16), ka= 0.25, kd= 0.65)
            reflective(id = "m3", cd= c(0.7), ka= 0.25, kd= 0.65)
        }

        objects {
            sphere(material = "m1", center = p(10, -5, 0), radius = 27.0)
            sphere(material = "m2", center = p(-30, 15, -50), radius = 27.0)
            plane(material = "m3")
        }
    }
}
