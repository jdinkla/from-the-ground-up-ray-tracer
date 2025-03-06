package net.dinkla.raytracer.examples.lights.ambient

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.acceleration.Acceleration
import net.dinkla.raytracer.samplers.Jittered
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.utilities.Ply
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition

const val NUM_AMBIENT_SAMPLES = 4

object World61 : WorldDefinition {
    override val id: String = "World61.kt"

    override fun world() =
        Builder.build {
            val sampler1 = Sampler(Jittered, 2500, 10)
            sampler1.mapSamplesToHemiSphere(1.0)

            camera(d = 1000.0, eye = p(0.0, 1.0, 5.0), lookAt = p(0.0, 1.0, 0.0))

            ambientOccluder(sampler = sampler1, numSamples = NUM_AMBIENT_SAMPLES)

            lights {
                pointLight(location = p(0.0, 100.0, 100.0), ls = 1.0, color = Color.WHITE)
            }

            materials {
                phong(id = "Green Yellow", cd = c("adff2f"), ka = 0.5, kd = 0.75, ks = 0.55, exp = 2.0)
                reflective(id = "mirror", cd = Color.WHITE, ka = 0.0, kd = 0.0, ks = 1.0)
                phong(id = "Moccasin", cd = c("ffe4b5"), ka = 0.25, kd = 0.75, ks = 0.35, exp = 20.0)
                phong(id = "sky", cd = c("87cefa"), ka = 0.5, kd = 0.75, ks = 0.1, exp = 22.0)
            }

            objects {
                val green = this.materials["mirror"]!!
                val stanfordBunny =
                    Ply.fromFile(
                        fileName = "resources\\Bunny4K.ply",
                        isSmooth = true,
                        type = Acceleration.GRID,
                        material = green,
                    )

                plane(material = "sky", point = p(0.0, 200.0, 0.0), normal = Normal.UP)
                plane(material = "Green Yellow", point = Point3D.ORIGIN, normal = Normal.UP)

                instance(of = stanfordBunny.compound, material = "Moccasin") {
                    rotate(Axis.Y, 0.0)
                    scale(v(15, 15, 15))
                    translate(v(0.0, -0.5, 0.0))
                }
            }
        }
}
