package net.dinkla.raytracer.examples.lights.area

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.arealights.RectangleLight
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object FourAreaShadedBoxes : WorldDefinition {
    override val id: String = "FourAreaShadedBoxes.kt"

    private const val NUM_SAMPLES = 16

    private val sampler1: Sampler by lazy {
        Sampler(MultiJittered, 2500, 100).apply {
            //mapSamplesToUnitDisk(
            mapSamplesToSphere()
        }
    }

    override fun world(): World = Builder.build {
        metadata {
            title("Four boxes")
            description("Use area tracer. Does not work.")
        }

        val vecH = Vector3D.UP * 2.0

        camera(d = 1000.0, eye = p(0.0, 3.0, 10.0), lookAt = p(0, 1, 0))

        ambientLight(ls = 0.0)

        materials {
            phong(id = "m1", cd = c(0.1, 0.8, 0.2), ka = 0.25, kd = 0.75, ks = 0.8)
            phong(id = "m2", cd = c(1.0, 1.0, 1.0), ka = 1.0, kd = 1.0, ks = 1.0)
            emissive(id = "em", ce = c(1.0, 0.9, 0.2), le = 1.0)
        }

        val r1 = RectangleLight(
            p0 = p(-5.0, 3.0, -5.0),
            a = Vector3D.RIGHT * 10.0,
            b = Vector3D.FORWARD * 10.0,
            sampler = sampler1,
        ).also {
            it.material = world.materials["em"]
        }

        objects {
            plane(material = "m1", point = p(0.0, -10.01, 0.0), normal = Normal.UP)
            box(p0 = p(-6, 0, 1), b = vecH, material = "m1")
            box(p0 = p(-2, 0, 1), b = vecH, material = "m1")
            box(p0 = p(2, 0, 1), b = vecH, material = "m1")
            box(p0 = p(6, 0, 1), b = vecH, material = "m1")
        }

        lights {
            areaLight(of = r1, numSamples = NUM_SAMPLES) // pointLight(location = p(0, 10, 0), ls = 1.0)
        }
    }
}
