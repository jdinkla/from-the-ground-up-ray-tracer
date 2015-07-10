import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3DF
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.samplers.MultiJittered

def NUM_AMBIENT_SAMPLES = 16

//String path = 'F://opt/rendering/ply'
String path = '/opt/rendering/ply'
//Grid bunny = builder.ply(file: "${path}/bunny/bunny69K.ply", multiplier: 2.0, smooth: true)
Grid bunny = builder.ply(file: "${path}/bunny/bunny4K.ply", multiplier: 2.0, smooth: true)

def sampler = new Sampler(new MultiJittered(), 2500, 10)
sampler.mapSamplesToHemiSphere(1.0)

builder.world(id: "World54") {

//    viewPlane(resolution: Resolution.RESOLUTION_1080, numSamples: 4, maxDepth: 5)
    viewPlane(resolution: Resolution.RESOLUTION_480, numSamples: 4, maxDepth: 5)

//    camera(d: 2000, eye: p(8, 1, 7), lookAt: p(11.2, 1, 0), type: PinholePar, numThreads: 30 )
    camera(d: 1000, eye: p(8, 1, 7), lookAt: p(11.2, 1, 0), numThreads: 30 )

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
        plane(material: "gray", point: Point3DF.ORIGIN, normal: Normal.UP)
        plane(material: "gray", point: p(0, 0, -5), normal: Normal.FRONT)

        instance(object: bunny, material: "chocolate") {
            scale(v(15, 15, 15))
            translate(v(10, 0, 0))
        }
    }

}
