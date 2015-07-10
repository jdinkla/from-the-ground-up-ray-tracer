package lights.ambient

import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3DF
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.utilities.Resolution;

def NUM_AMBIENT_SAMPLES = 4

String path = '/opt/rendering/ply'
Grid bunny = builder.ply(file: "${path}/bunny/bunny4K.ply", multiplier: 2.0, smooth: true)

def sampler = new Sampler(new MultiJittered(), 2500, 100)
sampler.mapSamplesToHemiSphere(1.0)

def sc = builder.solidCylinder(y0: 0, y1: 12.5, radius: 0.75)

builder.world(id: "X1") {

    viewPlane(resolution: Resolution.RESOLUTION_1080)

    camera(d: 1000, eye: p(0, 1, 10), lookAt: p(2, 0.75, 0), numThreads: 30 )

    ambientOccluder(minAmount: RGBColor.BLACK, sampler: sampler, numSamples: NUM_AMBIENT_SAMPLES)

    lights {

        pointLight(location: p(51, 200, 0), color: c(1, 1, 1), ls: 1)
//        pointLight(location: p(51.1, 200, 0), color: c(1, 1, 1), ls: 1)
//        pointLight(location: p(51.2, 200, 0), color: c(1, 1, 1), ls: 1)
//        pointLight(location: p(51.3, 200, 0), color: c(1, 1, 1), ls: 1)
//        pointLight(location: p(51.4, 200, 0), color: c(1, 1, 1), ls: 1)
        pointLight(location: p(100, 200, 100), color: c(1, 1, 1), ls: 1)
        pointLight(location: p(-100, 200, 100), color: c(1, 1, 1), ls: 1)
        pointLight(location: p(100, 200, -100), color: c(1, 1, 1), ls: 1)
        pointLight(location: p(-100, 200, -100), color: c(1, 1, 1), ls: 1)
    }

    materials {
//        phong(id: "yellow", cd: c(1, 1, 0), ka: 0.5, kd: 0.5, ks: 0.25, exp: 4)
//        phong(id: "sky", cd: c("6698FF"), ka: 0.5, kd: 0.5, ks: 0.25, exp: 4)
        phong(id: "sky", cd: c("1E2D5B"), ka: 1.0, kd: 1.0, ks: 1.0, exp: 4)

        matte(id: "gray", cd: c(1), ka: 0.5, kd: 0.5)
//        phong(id: "orange", cd: c(1, 0.5, 0), ka: 0.5, kd: 0.25, ks: 0.25, exp: 20)
        phong(id: "chocolate", cd: c(0.5647, 0.1294, 0), ka: 0.5, kd: 0.25, ks: 0.25, exp: 2)
        //reflective(id: "yellow_r", cr: c(1, 1, 0), kr: 0.7, ka: 0.5, kd: 0.5, ks: 0.25, cs: c(1, 1, 0), exp: 4)

        reflective(id: "yellow_r", cr: c("F7B685"), kr: 0.7, ka: 0.5, kd: 0.5, ks: 0.25, cs: c("F7B685"), exp: 4)


        reflective(id: "green_r", cr: c("6CC54F"), kr: 0.5, ka: 0.5, kd: 0.5, ks: 0.25, cs: c("6CC54F"), exp: 1)
        //reflective(id: "green_r", cr: c(0, 1, 0), kr: 0.5, ka: 0.5, kd: 0.5, ks: 0.25, cs: c(0, 1, 0), exp: 1)
        reflective(id: "red_r", cr: c("AF0A14"), kr: 0.5, ka: 0.5, kd: 0.5, ks: 0.25, cs: c("AF0A14"), exp: 1)
        reflective(id: "blue_r", cr: c("4F6CC5"), kr: 0.5, ka: 0.5, kd: 0.5, ks: 0.25, cs: c("4F6CC5"), exp: 1)

        reflective(id: "gray_r", cr: c(1), kr: 0.7, ka: 0.5, kd: 0.5, ks: 0.25, cs: c(1))

//        transparent(id: "trans", ks: 0.5, ka: 0.5, kd: 0.5, kt: 0.5, kr: 0.5, exp: 1, ior: 1.0, cd: c(1, 0, 0), cs: c(0, 1, 0), cr: c(0, 0, 1))

        glossy(id: "gX", ks: 0.75, cd: c("1E4C5C"), ka: 0.5, kd: 0.75, exp: 2, kr: 0.75, sampler: sampler)

//        glossy(id: "g3", ks: 0.75, cd: c(0.98, 0.76, 0.4), ka: 0.5, kd: 0.75, exp: 2, kr: 0.75, sampler: sampler)
//        glossy(id: "gy", ks: 0.25, cd: c(0.98, 0.96, 0.05), ka: 0.5, kd: 0.75, exp: 2, kr: 0.25, sampler: sampler)
//        glossy(id: "go", ks: 0.25, cd: c(0.98, 0.8, 0.7), ka: 0.5, kd: 0.25, exp: 20, kr: 0.25, sampler: sampler)

    }

    objects {
        plane(material: "gray_r", point: Point3DF.ORIGIN, normal: Normal.UP)
        plane(material: "sky", point: p(0, 1000, 0), normal: Normal.DOWN)
        //grid {
            sphere(material: "yellow_r", center: p(0, 2, 0), radius: 2)
            sphere(material: "red_r", center: p(1, 0.75, 4), radius: 0.75)

            alignedBox(material: "green_r", p: p(4, 0, 1), q: p(4+0.25, 0+3, 1+5))

            alignedBox(material: "blue_r", p: p(-1.5, 0, 6), q: p(-1.5+0.5, 0+0.5, 6+0.5))

            instance(object: sc, material: "gX") {
                translate(v(-3, 0, 3))
            }
        //}
    }

}
