package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.utilities.Ply
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object World57 : WorldDefinition {

    val sampler = Sampler(MultiJittered, 2500, 1000)

    private const val NUM_AMBIENT_SAMPLES = 4

    init {
        sampler.mapSamplesToHemiSphere(1.0)
    }

    override val id: String = "World57.kt"

    override fun world(): World = Builder.build {
        metadata {
            id(id)
        }

        // TODO camera(d: 1000, eye: p(8, 1, 7), lookAt: p(11.2, 1, 0), numThreads: 64, ray: SampledRenderer, raySampler: sampler, rayNumSamples: 2 )
        camera(d = 1000.0, eye = p(0, 1, -10), lookAt = p(0.0, 1.0, 0.0))

        ambientOccluder(minAmount = Color.WHITE, sampler = sampler, numSamples = NUM_AMBIENT_SAMPLES)

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
            fileName = "resources/bunny4K.ply",
            isSmooth = true,
            material = this.world.materials["chocolate"]!!
        )

        objects {
            plane(material = "orange", point = Point3D.ORIGIN, normal = Normal.UP)
            // plane(material = "yellow", point = p(0, 1000, 0), normal = Normal.DOWN)

            for (i in listOf(-10, -5, 0, 5, 10)) {
                instance(material = "chocolate", of = bunny.compound) {
                    scale(v(15.0, 15.0, 15.0))
                    translate(v(i.toDouble(), 0.0, 0.0))
                }
            }
        }
    }
}
