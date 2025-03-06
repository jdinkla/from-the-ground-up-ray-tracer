package net.dinkla.raytracer.examples.lights.area

import net.dinkla.raytracer.materials.Emissive
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.arealights.DiskLight
import net.dinkla.raytracer.samplers.Jittered
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition

@Suppress("unused")
object AreaShadedSpheres : WorldDefinition {
    override val id = "AreaShadedSpheres.kt"

    override fun world() =
        Builder.build {
            val numSamples = 128
            val sampler2 = Sampler(Jittered, 100, 100)
            sampler2.mapSamplesToUnitDisk()

            metadata {
                id("World66 with area")
                description("Use area tracer")
            }

            camera(d = 1500.0, eye = p(2.5, 1.35, 10.0), lookAt = p(2.5, 1.0, 0.0))

            ambientLight(ls = 0.75)

            materials {
                matte(id = "gray", cd = c(1.0), ka = 0.25, kd = 0.75)
                reflective(id = "mirror", cd = c("f0f0f0"), ka = 0.5, kd = 0.5, ks = 0.2, kr = 0.4, cr = c(1.0, 1.0, 1.0))
                phong(id = "Green Yellow", cd = c("adff2f"), ka = 0.5, kd = 0.75)
                phong(id = "Light Salmon", cd = c("ffa07a"), ka = 0.5, kd = 0.75, ks = 0.55, exp = 15.0)
                phong(id = "Pink", cd = c("ffc0cb"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 9.0)
                phong(id = "Gold1", cd = c("FFD700"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 5.0)
                phong(id = "Gold2", cd = c("EEC900"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 5.0)
            }

            val disk =
                DiskLight(
                    sampler = sampler2,
                    center = p(30.0, 5.0, -0.0),
                    radius = 40.0,
                    normal = Normal.LEFT,
                ).apply {
                    this.material = Emissive()
                }

            lights {
                areaLight(of = disk, numSamples = numSamples)
                directionalLight(direction = v(-0.9, -0.2, -0.9), ls = 0.5, color = c("ffc0cb"))
            }

            objects {
                plane(material = "mirror")
                sphere(center = p(0, 1, 0), radius = 1.0, material = "Green Yellow")
                sphere(center = p(2, 1, -10), radius = 1.0, material = "Light Salmon")
                sphere(center = p(4, 1, -20), radius = 1.0, material = "Pink")
                sphere(center = p(6, 1, -30), radius = 1.0, material = "Gold1")
                sphere(center = p(8, 1, -40), radius = 1.0, material = "Gold2")
            }
        }
}
