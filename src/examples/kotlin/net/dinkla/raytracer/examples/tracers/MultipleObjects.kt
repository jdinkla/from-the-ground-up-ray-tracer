package net.dinkla.raytracer.examples.tracers

import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object MultipleObjects : WorldDefinition {
    override val id: String = "MultipleObjects.kt"

    override fun world(): World =
        Builder.build {
            metadata {
                description("Use MultipleObjects tracer")
                preferredTracer(Tracers.MULTIPLE_OBJECTS)
            }

            camera(d = 2000.0, eye = p(40, 30, 250), lookAt = p(40, 20, 0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(100, 100, 200), ls = 3.0)
            }

            materials {
                matte(id = "m1", cd = c(1.0, 0.0, 0.0))
                matte(id = "m2", cd = c(0.0, 1.0, 0.0))
                matte(id = "m3", cd = c(0.0, 0.0, 1.0))
            }

            objects {
                sphere(material = "m1", center = p(0, 50, 0), radius = 10.0)
                sphere(material = "m2", center = p(20, 10, 0), radius = 10.0)
                sphere(material = "m3", center = p(80, 0, 0), radius = 30.0)
            }
        }
}
