package net.dinkla.raytracer.examples.tracers

import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object MultipleObjects : WorldDefinition {
    override val id: String = "MultipleObjects.kt"

    override fun world(): World = Builder.build {
        metadata {
            description("Use MultipleObjects tracer")
        }

        camera(d = 750.0, eye = p(0, 0, 200), lookAt = p(50, 0, 0))

        ambientLight(ls = 0.5)

        lights {
            pointLight(location = p(3, 3, 1))
        }

        materials {
            matte(id = "m1", cd = c(1, 0, 0))
            matte(id = "m2", cd = c(0, 1, 0))
            matte(id = "m3", cd = c(0, 0, 1))
        }

        objects {
            sphere(material = "m1", center = p(0, 50, 0), radius = 10.0)
            sphere(material = "m2", center = p(20, 10, 0), radius = 10.0)
            sphere(material = "m3", center = p(80, 0, 0), radius = 30.0)
        }
    }
}
