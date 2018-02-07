package lights.ambient

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.samplers.MultiJittered;

def NUM_AMBIENT_SAMPLES = 16

String path = '/opt/rendering/ply'
Grid bunny = builder.ply(file: "${path}/bunny/bunny4K.ply", multiplier: 2.0, smooth: true)

def sampler = new Sampler(new MultiJittered(), 2500, 100)
sampler.mapSamplesToHemiSphere(1.0)

builder.world(id: "World54") {

    camera(d: 1000, eye: p(0, 1, 10), lookAt: p(0, 0.75, 0), numThreads: 30 )

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
        phong(id: "chocolate", cd: c(0.5647, 0.1294, 0), ka: 0.5, kd: 0.25, ks: 0.55, exp: 2)
    }

    objects {
        sphere(material: "yellow", center: p(0, 2, 0), radius: 2)
        sphere(material: "orange", center: p(1, 0.75, 4), radius: 0.75)
        plane(material: "gray", point: Point3D.ORIGIN, normal: Normal.UP)

        instance(object: bunny, material: "chocolate") {
            scale(v(5, 5, 5,))
            translate(v(-1.5, 0, 6))
        }
    }

}
