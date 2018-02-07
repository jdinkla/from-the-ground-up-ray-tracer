// for testing the various camera types

import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.cameras.lenses.ThinLens
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.cameras.lenses.Orthographic
import net.dinkla.raytracer.cameras.lenses.FishEye
import net.dinkla.raytracer.cameras.lenses.Spherical

def b1 = builder.alignedBox(p: p(0, 0, 0), q: p(1, 2, 1))

int numProcs = Runtime.getRuntime().availableProcessors();

def samp1 = new Sampler(new MultiJittered(), 2500, 10)
samp1.mapSamplesToUnitDisk()

builder.world(id: "World58") {

    viewPlane(numSamples: 0)

    camera(eye: p(2, 1, 10), lookAt: p(2, 1, 0), d: 1000, type: ThinLens, f: 1000, lensRadius: 1, sampler: samp1, numThreads: 16)

    //camera(eye: p(2, 1, 10), lookAt: p(2, 1, 0), d: 1000)
    //camera(eye: p(2, 1, 10), lookAt: p(2, 1, 0), d: 1000, numThreads: 4)
    //camera(eye: p(2, 1, 10), lookAt: p(2, 1, 0), d: 1000, type: ThinLens, f: 222, lensRadius: 1, sampler: samp1, numThreads: 16)
//    camera(eye: p(2, 1, 10), lookAt: p(2, 1, 0), type: Orthographic)
//    camera(eye: p(2, 1, 10), lookAt: p(2, 1, 0), type: FishEye, maxPsi: 120)
//    camera(eye: p(2, 1, -1), lookAt: p(2, 1, 0), type: Spherical, maxPsi: 180, maxLambda: 180, d: 10000, numThreads: 5)

//    ambientLight(ls: 0.5, color: c(1, 0, 0))
    ambientLight(ls: 0.5)

    lights {
        pointLight(location: p(4.5, 3, 7), ls: 1.0)
//        pointLight(location: p(4.5, 3, 7), ls: 1.0, color: c(1, 1, 0))
        //directional(direction: v(-1, -1, 0), color: c(1, 0, 0))
    }

    materials {
        phong(id: "m1", ks: 1.0, cd: c(1, 1, 1), ka: 0.4, kd: 0.9, exp: 1)
        phong(id: "m2", ks: 0.5, cd: c(0.1, 0.7, 0.3), ka: 0.25, kd: 0.75, exp: 10)
        phong(id: "m3", ks: 0.5, cd: c(1, 1, 0), ka: 0.25, kd: 0.75, exp: 50)
        phong(id: "m4", ks: 0.1, cd: c(1, 0, 0), ka: 0.25, kd: 0.75, exp: 3)
        phong(id: "m5", ks: 0.5, cd: c(0, 0.5, 1), ka: 0.25, kd: 0.75, exp: 10)
        matte(id: "black", cd: c(0, 0, 0))
//        phong(id: "m1", ks: 1.0, cd: c(1, 1, 1), ka: 0.4, kd: 0.9, exp: 1, cs: RGBColor.WHITE)
//        phong(id: "m2", ks: 0.5, cd: c(0.1, 0.7, 0.3), ka: 0.25, kd: 0.75, exp: 10, cs: RGBColor.WHITE)
//        phong(id: "m3", ks: 0.5, cd: c(1, 1, 0), ka: 0.25, kd: 0.75, exp: 50, cs: RGBColor.WHITE)
//        phong(id: "m4", ks: 0.1, cd: c(1, 0, 0), ka: 0.25, kd: 0.75, exp: 3, cs: RGBColor.WHITE)
//        phong(id: "m5", ks: 0.5, cd: c(0, 0.5, 1), ka: 0.25, kd: 0.75, exp: 10, cs: RGBColor.WHITE)
//        matte(id: "black", cd: c(0, 0, 0))
    }

    objects {
         plane(material: "m1", point: Point3D.ORIGIN, normal: Normal.UP)
         plane(material: "m1", point: p(0, 0, -700), normal: Normal.BACK)

        instance(object: b1, material: "m4") {
            translate(v(0, 0, 0))
        }

        instance(object: b1, material: "m2") {
            translate(v(2, 0, -20))
        }

        instance(object: b1, material: "m3") {
            translate(v(6, 0, -50))
        }

    }

}
