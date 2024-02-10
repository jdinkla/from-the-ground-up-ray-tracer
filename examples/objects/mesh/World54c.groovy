package lights.ambient

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.utilities.Resolution;

def NUM_AMBIENT_SAMPLES = 4

String path = '/opt/rendering/ply'
Grid bunny = builder.ply(file: "${path}/bunny/bunny16K.ply", multiplier: 2.0, smooth: true)

def sampler = new Sampler(new MultiJittered(), 2500, 100)
sampler.mapSamplesToHemiSphere(1.0)

builder.world(id: "World54") {

    viewPlane(resolution: Resolution.RESOLUTION_1440)

    camera(d: 2000, eye: p(0, 1, 10), lookAt: p(0, 0.75, 0), numThreads: 30 )

    //ambientOccluder(minAmount: RGBColor.WHITE, sampler: sampler, numSamples: NUM_AMBIENT_SAMPLES)
    ambientOccluder(minAmount: Color.BLACK, sampler: sampler, numSamples: NUM_AMBIENT_SAMPLES)

    lights {
        pointLight(location: p(-5, 5, 0), color: c(1, 0, 0), ls: 1)
        pointLight(location: p(5, 5, 0), color: c(0, 0, 1), ls: 1)
        pointLight(location: p(5, 5, -15), color: c(0, 1, 0), ls: 1)
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
