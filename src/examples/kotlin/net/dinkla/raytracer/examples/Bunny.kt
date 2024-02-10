package net.dinkla.raytracer.examples

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.utilities.Ply
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object Bunny : WorldDefinition {
    override val id: String = "Bunny.kt"

    val sampler = Sampler(MultiJittered, 2500, 100).apply {
        mapSamplesToHemiSphere(1.0)
    }

    override fun world(): World = Builder.build {
        metadata {
            description("This can take longer")
        }

        camera(d = 2000.0, eye = p(0, 1, 10), lookAt = p(0.0, 1.0, 0.0))

        ambientOccluder(sampler = sampler, numSamples = 1)

        lights {
            pointLight(location = p(0, 5, 5), ls = 1.0)
        }

        materials {
            reflective(id = "gray", cd = c(1.0), ka = 0.5, kd = 0.5)
            phong(id = "yellow", cd = c(1, 1, 0), ka = 0.5, kd = 0.5, ks = 0.25, exp = 4.0)
            phong(id = "orange", cd = c(1.0, 0.5, 0.0), ka = 0.5, kd = 0.25, ks = 0.55, exp = 2.0)
            phong(id = "chocolate", cd = c(0.5647, 0.1294, 0.0), ka = 0.5, kd = 0.25, ks = 0.55, exp = 2.0)
        }

        val bunny = Ply.fromFile(
            fileName = "resources/Bunny4K.ply",
            isSmooth = true,
            material = world.materials["chocolate"]!!
        )

        objects {
            plane(material = "orange", point = Point3D.ORIGIN, normal = Normal.UP)
            for (i in listOf(0)) {
                instance(material = "chocolate", of = bunny.compound) {
                    scale(v(15.0, 15.0, 15.0))
                    translate(v(i.toDouble(), 0.0, 0.0))
                }
            }
        }
    }
}
