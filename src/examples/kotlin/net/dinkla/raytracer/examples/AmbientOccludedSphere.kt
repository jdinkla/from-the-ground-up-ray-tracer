package net.dinkla.raytracer.examples

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.samplers.Constant
import net.dinkla.raytracer.samplers.Hammersley
import net.dinkla.raytracer.samplers.Jittered
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.samplers.PureRandom
import net.dinkla.raytracer.samplers.Regular
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object AmbientOccludedSphere : WorldDefinition {
    override val id: String = "AmbientOccludedSphere.kt"

    private const val numberOfSamples = 512
    private const val numberOfAmbientSamples = 32
    private const val numberOfSets = 10

    val s = listOf(PureRandom, Hammersley, Jittered, Regular, Constant(), MultiJittered)
    val sampler =
        Sampler(s[0], numberOfSamples, numberOfSets).apply {
            mapSamplesToHemiSphere(3.0)
        }

    override fun world(): World =
        Builder.build {
            camera(d = 1000.0, eye = p(0.0, 1.0, 7.0), lookAt = p(0.0, 0.75, 0.0))

            ambientOccluder(
                sampler = sampler,
                numSamples = numberOfAmbientSamples,
            )

            lights {
            }

            materials {
                matte(id = "m1", cd = c(1.0, 1.0, 0.0), ka = 0.75, kd = 0.0)
                matte(id = "m2", cd = c(1.0), ka = 0.75, kd = 0.0)
            }

            objects {
                sphere(material = "m1", center = p(0.0, 1.0, 0.0), radius = 1.0)
                plane(material = "m2", point = Point3D.ORIGIN, normal = Normal.UP)
            }
        }
}
