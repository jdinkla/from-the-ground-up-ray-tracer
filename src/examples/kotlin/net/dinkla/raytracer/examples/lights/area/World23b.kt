package net.dinkla.raytracer.examples.lights.area

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.materials.Emissive
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.arealights.RectangleLight
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition


const val numSamples = 16

object World23b : WorldDefinition {
    override val id: String = "World23b.kt"

    private val sampler1: Sampler by lazy {
        Sampler(MultiJittered, 2500, 100).also {
            it.mapSamplesToUnitDisk()
        }
    }

    override fun world(): World = Builder.build {
        metadata {
            id(id)
            description("Use area tracer")
        }

        val vecW = v(1, 0, 0)
        val vecH = v(0, 2, 0)
        val vecD = v(0, 0, 1)

        camera( eye= p(0.0, 1.5, 10.0), lookAt= p(0, 1, 0))
        //camera(direction= 500, eye= p(0, 1.5, 10), lookAt= p(0, 1, 0), type= FishEye, maxPsi= 180)
        //camera(eye = p(0.0, 1.5, 10.0), lookAt = p(0, 1, 0))

        ambientLight(color = Color.WHITE, ls = 0.5)

        materials {
            phong(id = "m1", cd = c(0.1, 0.8, 0.2), ka = 0.25, kd = 0.75, ks = 0.8)
            phong(id = "m2", cd = c(1.0, 1.0, 1.0), ka = 1.0, kd = 1.0, ks = 1.0)
            emissive(id = "em", ce = c(1.0, 0.9, 0.2), le = 1.0)
        }

        val r1 = RectangleLight(
            p0 = p(-10.75, 2.0, -10.0),
            a = vecW.times(15.5),
            b = vecH.times(2.0),
            sampler = sampler1, //material = "em"
        ).also {
            it.material = Emissive(ce = c(1.0, 0.9, 0.2), ls = 1.0)
        }

        objects {
            box(p0 = p(-6, 0, 1), a = vecW, b = vecH, c = vecD, material = "m1")
            box(p0 = p(-2, 0, 1), a = vecW, b = vecH, c = vecD, material = "m1")
            box(p0 = p(2, 0, 1), a = vecW, b = vecH, c = vecD, material = "m1")
            box(p0 = p(6, 0, 1), a = vecW, b = vecH, c = vecD, material = "m1")
            plane(material = "m2", point = Point3D.ORIGIN, normal = Normal.UP)
            r1 // r1 = disk(center= p(0, 5, -10), radius= 2, normal= n(0, 0, 1), sampler= sampler1, material= "em")
        }

        lights {
            areaLight(of = r1, numSamples = numSamples)
        }

    }
}
