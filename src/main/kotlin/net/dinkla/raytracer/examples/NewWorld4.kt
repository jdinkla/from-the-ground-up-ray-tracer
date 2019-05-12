package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.objects.arealights.DiskLight
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.tracers.Tracers
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDef

object NewWorld4 : WorldDef {

    override fun world(): World = Builder.build("New World 3 - emissive") {

        camera(d = 1000.0, eye = p(-5.0, 1.5, -5.0), lookAt = p(0.5, 0.5, 0.0), tracer = Tracers.AREA)

        ambientLight(ls = 0.5)

//        lights {
//            directionalLight(direction = v(1.0, -1.0, -0.5), ls = 2.0, color = Color.WHITE)
//        }

        materials {
            matte(id = "yellow", cd = c("FFCC00"), ka = 0.25, kd = 0.65)
            emissive(id = "em", ce = c(1.0), le = 12.0)
        }

        val sampler2 = Sampler(MultiJittered, 100, 100)
        sampler2.mapSamplesToUnitDisk()
        val disk = DiskLight(sampler = sampler2,
                center = p(0.5, 20.0, -2.0), radius = 10.0, normal = Normal(Vector3D.DOWN + Vector3D.FORWARD)).apply {
            this.material = world.materials["em"]
        }

        lights {
            //            areaLight(of = rectangle, numSamples = numSamples)
            areaLight(of = disk, numSamples = World20area.numSamples)
        }
        objects {
            alignedBox(material = "yellow", p = Point3D.ORIGIN, q = Point3D.UNIT)
            alignedBox(material = "yellow", p = p(1.0, 0.0, -1.0), q = p(1.0, 0.0, -1.0) + Vector3D.JITTER)
        }
    }
}