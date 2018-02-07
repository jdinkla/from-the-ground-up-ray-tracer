import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.samplers.MultiJittered;

def NUM_AMBIENT_SAMPLES = 4

//String path = 'F://opt/rendering/ply'
String path = '/opt/rendering/ply'
//Grid bunny = builder.ply(file: "${path}/bunny/bunny69K.ply", multiplier: 2.0, smooth: true)
Grid bunny = builder.ply(file: "${path}/bunny/bunny4K.ply", multiplier: 2.0, smooth: true)

def sampler = new Sampler(new MultiJittered(), 2500, 100)
sampler.mapSamplesToHemiSphere(1.0)

builder.world(id: "World54") {

    camera(d: 1000, eye: p(0, 1, 10), lookAt: p(2, 0.75, 0), numThreads: 30 )

    ambientOccluder(minAmount: Color.WHITE,
        sampler: sampler,
        numSamples: NUM_AMBIENT_SAMPLES)

    lights {
        pointLight(location: p(0, 5, 5), color: c(1, 1, 1), ls: 1)
    }

    materials {
        phong(id: "yellow", cd: c(1, 1, 0), ka: 0.5, kd: 0.5, ks: 0.25, exp: 4)
        matte(id: "gray", cd: c(1), ka: 0.5, kd: 0.5)
        phong(id: "orange", cd: c(1, 0.5, 0), ka: 0.5, kd: 0.25, ks: 0.25, exp: 20)
    }

    objects {
        sphere(material: "yellow", center: p(0, 1, 0), radius: 1)
        sphere(material: "orange", center: p(3, 1, -1), radius: 1)
        plane(material: "gray", point: Point3D.ORIGIN, normal: Normal.UP)

        instance(object: bunny, material: "yellow") {
            scale(v(3, 3, 3))
            translate(v(1.5, 0, 2))
        }
    }

}
