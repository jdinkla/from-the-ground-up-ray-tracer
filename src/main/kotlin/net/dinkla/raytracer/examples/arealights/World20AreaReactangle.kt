package net.dinkla.raytracer.examples.arealights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.arealights.RectangleLight
import net.dinkla.raytracer.samplers.PureRandom
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder.build
import net.dinkla.raytracer.world.WorldDefinition

object World20AreaReactangle : WorldDefinition {

    const val numSamples = 32

    override fun world() = build("World 20 area") {

        camera(d = 1500.0, eye = p(2.0, 0.5, 5.0), lookAt = p(1.5, 1.0, 0.0), tracer = Tracers.AREA)

        ambientLight(color = Color.WHITE, ls = 0.0)

        materials {
            phong(id = "yellow", ka = 0.75, kd = 0.25, cd =  c("CC8400"), exp = 2.0, ks = 0.98, cs = c("FFA500") * 0.1 + c("CC8400") * 0.9)
            phong(id = "grey", ka = 0.75, kd = 0.25, cd = c(1.0), exp = 10.0, ks = 0.9, cs = Color.WHITE)
            phong(id = "red", ka = 0.5, kd = 0.5, cd = c(1.0, 0.0, 0.0), exp = 10.0, ks = 0.9, cs = Color.WHITE)
            emissive(id = "em", ce = c(1.0), le = 2.0)
        }

        val sampler1 = Sampler(PureRandom, 100, 100)
        sampler1.mapSamplesToUnitDisk()

        val vecW = v(1, 0, 0)
        val vecH = v(0, 1, 0)

        val rectangle = RectangleLight(sampler = sampler1,
                p0 = p(-2, 1, -10), a = vecW * 4.0, b = vecH * 5.0, normal = Normal.DOWN).apply {
            this.material = world.materials["em"]
        }

        lights {
            areaLight(of = rectangle, numSamples = numSamples)
        }

        objects {
            sphere(material = "yellow", center = p(0, 1, 0), radius = 1.0)
            sphere(material = "red", center = p(3, 1, 0), radius = 1.0)
            plane(material = "grey", point = Point3D.ORIGIN, normal = Normal.UP)
            plane(material = "grey", point = p(0, 20, 0), normal = Normal.DOWN)
        }
    }
}