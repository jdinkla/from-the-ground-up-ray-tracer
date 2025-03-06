package net.dinkla.raytracer.examples.lights.area

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.arealights.RectangleLight
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object World60 : WorldDefinition {
    override val id: String = "World60.kt"

    private const val NUM_SAMPLES = 16

    private val sampler1 =
        Sampler(MultiJittered, 2500, 100).apply {
            mapSamplesToUnitDisk()
        }

    override fun world(): World =
        Builder.build {
            metadata {
                description("does not work")
            }
            camera(d = 1000.0, eye = p(20, 10, 40), lookAt = p(20, 10, 0))

            ambientLight(color = Color.WHITE, ls = 1.0)

            materials {
                phong(id = "green", cd = c(0, 1, 0), ka = 0.0, kd = 0.75, ks = 0.25, exp = 10.0)
                matte(id = "gray", cd = c(0.5, 0.5, 0.5), ka = 0.25, kd = 0.75)
                emissive(id = "emissive", ce = c(1.0, 0.0, 1.0), le = 1.0)
            }

            val r1 =
                RectangleLight(p0 = p(9.5, 20.0, -0.5), a = v(1, 0, 0), b = v(0, 0, 1), sampler = sampler1).apply {
                    material = world.materials["emissive"]
                }
            val r2 =
                RectangleLight(p0 = p(26.5, 20.0, -3.5), a = v(7, 0, 0), b = v(0, 0, 7), sampler = sampler1).apply {
                    material = world.materials["emissive"]
                }

            objects {
                plane(material = "gray", point = Point3D.ORIGIN, normal = Normal.UP)
                sphere(center = p(10, 10, 0), radius = 2.5, material = "green")
                sphere(center = p(10, 30, 0), radius = 2.5, material = "green")
                sphere(center = p(30, 10, 0), radius = 2.5, material = "green")
                sphere(center = p(30, 30, 0), radius = 2.5, material = "green")
                plane(material = "gray", point = p(20, 0, 0), normal = Normal.RIGHT)
                rectangle(p0 = p(9.5, 20.0, -0.5), a = v(1, 0, 0), b = v(0, 0, 1), material = "emissive")
                rectangle(p0 = p(26.5, 20.0, -3.5), a = v(7, 0, 0), b = v(0, 0, 7), material = "emissive")
            }

            lights {
                areaLight(of = r1, numSamples = NUM_SAMPLES)
                areaLight(of = r2, numSamples = NUM_SAMPLES)
            }
        }
}
