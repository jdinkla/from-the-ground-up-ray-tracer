package net.dinkla.raytracer.examples.cameras

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.AlignedBox
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

val samp1: Sampler by lazy {
    Sampler(net.dinkla.raytracer.samplers.MultiJittered, 2500, 10).also {
        it.mapSamplesToUnitDisk()
    }
}

object World58 : WorldDefinition {
    override val id: String = "World58.kt"

    override fun world(): World =
        Builder.build {
            val b1 = AlignedBox(p = p(0, 0, 0), q = p(1, 2, 1))
            camera(
                d = 1000.0,
                eye = p(2, 1, 10),
                lookAt = p(2, 1, 0),
            )

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(4.5, 3.0, 7.0), ls = 1.0)
            }

            materials {
                phong(id = "m1", ks = 1.0, cd = c(1, 1, 1), ka = 0.4, kd = 0.9, exp = 1.0)
                phong(id = "m2", ks = 0.5, cd = c(0.1, 0.7, 0.3), ka = 0.25, kd = 0.75, exp = 10.0)
                phong(id = "m3", ks = 0.5, cd = c(1, 1, 0), ka = 0.25, kd = 0.75, exp = 50.0)
                phong(id = "m4", ks = 0.1, cd = c(1, 0, 0), ka = 0.25, kd = 0.75, exp = 3.0)
                phong(id = "m5", ks = 0.5, cd = c(0.0, 0.5, 1.0), ka = 0.25, kd = 0.75, exp = 10.0)
                matte(id = "black", cd = c(0, 0, 0))
            }

            objects {
                plane(material = "m1", point = Point3D.ORIGIN, normal = Normal.UP)
                plane(material = "m1", point = p(0, 0, -700), normal = Normal.BACKWARD)
                instance(of = b1, material = "m4") {
                    translate(v(0, 0, 0))
                }
                instance(of = b1, material = "m2") {
                    translate(v(2, 0, -20))
                }
                instance(of = b1, material = "m3") {
                    translate(v(6, 0, -50))
                }
            }
        }
}
