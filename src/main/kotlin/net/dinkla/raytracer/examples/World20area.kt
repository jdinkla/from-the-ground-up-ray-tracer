package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.arealights.RectangleLight
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder.build
import net.dinkla.raytracer.world.WorldDef

object World20area : WorldDef {

    val numSamples = 16

    override fun world() = build("World 20 area") {

        camera(d = 1500.0, eye = p(2.0, 0.5, 5.0), lookAt = p(1.5, 1.0, 0.0), tracer = Tracers.AREA)

        ambientLight(color = Color.WHITE, ls = 0.25)

        materials {
            matte(id = "m1", ka = 0.75, kd = 0.75, cd = c(1.0, 1.0, 0.0))
            matte(id = "m2", ka = 0.75, kd = 0.75, cd = c(1.0))
            phong(id = "m3", ka = 0.25, kd = 0.55, cd = c(1.0, 0.0, 0.0), exp = 10.0, ks = 0.9, cs = Color.WHITE)
            emissive(id = "em", ce = c(1.0, 0.9, 0.2), le = 2.0)
        }

        val vecW = v(1, 0, 0)
        val vecH = v(0, 1, 0)
        //val vecD = v(0.0, 1.0, 0.0)

        val sampler1 = Sampler(MultiJittered, 100, 100)

        val rl = RectangleLight(p0 = p(-2, 1, -10), a = vecW * 4.0, b = vecH * 5.0).apply {
            this.sampler = sampler1
            this.material = world.materials["em"]
        }

        lights {
            areaLight(of = rl, numSamples = numSamples)
        }

        objects {
            sphere(material = "m1", center = p(0, 1, 0), radius = 1.0)
            sphere(material = "m3", center = p(3, 1, 0), radius = 1.0)
            plane(material = "m2", point = Point3D.ORIGIN, normal = Normal.UP)
            plane(material = "m2", point = p(0, 20, 0), normal = Normal.DOWN)
        }
    }
}