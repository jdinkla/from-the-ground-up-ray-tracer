//
// !!! DO NOT CHANGED. USED FOR BENCHMARKS !!!
//
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.cameras.render.SampledRenderer

/*
30 13373 ms

PureRandom
39648 ms
35281 ms
37309 ms

35418 ms
32406 ms


mbp 38788

-- 25747 ms
-- numThreads: 8 12077 ms
-- numThreads: 16 11453 ms
-- numThreads: 64 11134 ms
 */
def NUM_AMBIENT_SAMPLES = 4

//String path = 'F://opt/rendering/ply'
String path = '/opt/rendering/ply'
Grid bunny = builder.ply(file: "${path}/bunny/bunny4K.ply", multiplier: 2.0, smooth: true)

def sampler = new Sampler(new MultiJittered(), 2500, 1000)
sampler.mapSamplesToHemiSphere(1.0)

builder.world(id: "World57") {

    viewPlane(resolution: Resolution.RESOLUTION_320, maxDepth: 2)

//    camera(d: 1000, eye: p(8, 1, 7), lookAt: p(11.2, 1, 0) )
    camera(d: 1000, eye: p(8, 1, 7), lookAt: p(11.2, 1, 0), numThreads: 64, ray: SampledRenderer, raySampler: sampler, rayNumSamples: 2 )

    ambientOccluder(minAmount: RGBColor.WHITE,
        sampler: sampler,
        numSamples: NUM_AMBIENT_SAMPLES)

    lights {
        pointLight(location: p(0, 5, 5), color: c(1, 1, 1), ls: 1)
    }

    materials {
        reflective(id: "gray", cd: c(1), ka: 0.5, kd: 0.5)
        phong(id: "yellow", cd: c(1, 1, 0), ka: 0.5, kd: 0.5, ks: 0.25, exp: 4)
        phong(id: "orange", cd: c(1, 0.5, 0), ka: 0.5, kd: 0.25, ks: 0.55, exp: 2)
        phong(id: "chocolate", cd: c(0.5647, 0.1294, 0), ka: 0.5, kd: 0.25, ks: 0.55, exp: 2)
    }

    objects {
        plane(material: "gray", point: Point3D.ORIGIN, normal: Normal.UP)
        plane(material: "gray", point: p(0, 0, -5), normal: Normal.FRONT)

        instance(object: bunny, material: "chocolate") {
            scale(v(15, 15, 15))
            translate(v(10, 0, 0))
        }
    }

}
