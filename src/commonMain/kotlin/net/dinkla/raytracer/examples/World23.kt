package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.arealights.RectangleLight
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.world.Builder.build
import net.dinkla.raytracer.world.WorldDefinition

object World23 : WorldDefinition {

    override val id: String = "World23.kt"

    private const val numSamples = 4

    override fun world() = build {

//    camera(direction: 500, eye: p(0, 1.5, 10), lookAt: p(0, 1, 0), numThreads: 20)
        // TODO type and maxPSi camera(direction: 500, eye: p(0, 1.5, 10), lookAt: p(0, 1, 0), type: FishEye, maxPsi: 180)
        //camera(eye= p(0.0, 1.5, 10.0), lookAt= p(0, 1, 0), type = FishEye, maxPsi = 180)
        camera(eye = p(0.0, 1.5, 10.0), lookAt = p(0, 1, 0))

        ambientLight(color = Color.WHITE, ls = 0.5)

        materials {
            phong(id = "m1", cd = c(0.1, 0.8, 0.2), ka = 0.25, kd = 0.75, ks = 0.8)
//        phong(id= "m2", cd= c(0.4), ka= 0.25, kd= 0.75, ks= 1.0)
            phong(id = "m2", cd = c(1.0, 1.0, 1.0), ka = 1.0, kd = 1.0, ks = 1.0)
//        emissive(id= "em", ce= c(1.0, 0.9, 0.2), le= 0.2)
            emissive(id = "em", ce = c(1.0, 0.9, 0.2), le = 1.0)
        }

        val vecW = v(1, 0, 0)
        val vecH = v(0, 2, 0)
        val vecD = v(0, 0, 1)

        val sampler1 = Sampler(MultiJittered, 2500, 100)
        sampler1.mapSamplesToUnitDisk()

        val rl = RectangleLight(sampler1, p0 = p(-10.75, 2.0, -10.0), a = vecW * 15.5, b = vecH * 2.0).apply {
            this.material = world.materials["em"]
        }

        lights {
            areaLight(of = rl, numSamples = numSamples)
        }

        objects {
//            box(p0 = p(-6, 0, 1), a= vecW, b= vecH, c= vecD, material= "m1")
//            box(p0 = p(-2, 0, 1), a= vecW, b= vecH, c= vecD, material= "m1")
//            box(p0 = p(2, 0, 1), a= vecW, b= vecH, c= vecD, material= "m1")
//            box(p0 = p(6, 0, 1), a= vecW, b= vecH, c= vecD, material= "m1")
            plane(material= "m2", point= Point3D.ORIGIN, normal= Normal.UP)
            sphere(material = "m1")
            sphere(material = "m2", center = p(2, 2, 2))
//            r1 = rectangleLight(p0= p(-10.75f, 2, -10), a= vecW.times(15.5f), b= vecH.times(2), sampler= sampler1, material= "em")
//        r1 = disk(center= p(0, 5, -10), radius= 2, normal= n(0, 0, 1), sampler= sampler1, material= "em")
        }


    }
}