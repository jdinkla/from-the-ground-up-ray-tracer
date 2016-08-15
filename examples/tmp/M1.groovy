package tmp

import net.dinkla.raytracer.math.Point3DF
import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.utilities.Resolution$ as OrigResolution

class Resolution {
    static def RESOLUTION_720 = OrigResolution.MODULE$.RESOLUTION_720()
}

def NUM_AMBIENT_SAMPLES = 64
//Grid bunny = builder.ply(file: "/opt/rendering/ply/bunny/bunny4K.ply", multiplier: 2.0, smooth: true)
def sampler = new Sampler(new MultiJittered(), 2500, 100)
sampler.mapSamplesToHemiSphere(1.0)

def sc = builder.solidCylinder(y0: 0, y1: 12.5, radius: 0.75)

builder.world(id: "X2") {

    viewPlane(resolution: Resolution.RESOLUTION_720, maxDepth: 10)

    camera(d: 100, eye: p(0, 1, 10), lookAt: p(0, 0.5, 0), numThreads: 60)

    ambientOccluder(minAmount: RGBColor.BLACK, sampler: sampler, numSamples: NUM_AMBIENT_SAMPLES)

    lights {
//        pointLight(location: p(0, 200, 300), color: c(1, 1, 1), ls: 1)
//        pointLight(location: p(0+0.1, 200, 300), color: c(1, 1, 1), ls: 1)
//        pointLight(location: p(0+0.2, 200, 300), color: c(1, 1, 1), ls: 1)
//        pointLight(location: p(0+0.3, 200, 300), color: c(1, 1, 1), ls: 1)
////        pointLight(location: p(100, 200, 100), color: c(1, 1, 1), ls: 1)
//        pointLight(location: p(-100, 200, 100), color: c(1, 1, 1), ls: 1)
    }

    materials {
        phong(id: "sky", cd: c("1E2D5B"), ka: 1.0, kd: 1.0, ks: 1.0, exp: 4)
        reflective(id: "yellow_r", cr: c("F7B685"), kr: 0.7, ka: 0.5, kd: 0.5, ks: 0.25, cs: c("F7B685"), exp: 4)
        reflective(id: "green_r", cr: c("6CC54F"), kr: 0.5, ka: 0.5, kd: 0.5, ks: 0.25, cs: c("6CC54F"), exp: 1)
        phong(id: "red_r", cr: c("AF0A14"), kr: 0.5, ka: 0.5, kd: 0.5, ks: 0.25, cs: c("AF0A14"), exp: 1)
        reflective(id: "blue_r", cr: c("4F6CC5"), kr: 0.5, ka: 0.5, kd: 0.5, ks: 0.25, cs: c("4F6CC5"), exp: 1)
        phong(id: "gray_r", cr: c(1), kr: 0.7, ka: 0.5, kd: 0.5, ks: 0.25, cs: c(1))
        reflective(id: "purple_r", cr: c("9E8AD9"), kr: 0.7, ka: 0.5, kd: 0.5, ks: 0.25, cs: c("9E8AD9"), exp: 2)
        reflective(id: "turq_r", cr: c("1E4C5C"), kr: 0.8, ka: 0.5, kd: 0.5, ks: 0.25, cs: c("1E4C5C"), exp: 2)
    }

    objects {
        plane(material: "gray_r", point: Point3DF.ORIGIN, normal: Normal.UP)
      //  plane(material: "sky", point: p(0, 1000, 0), normal: Normal.DOWN)

        sphere(material: "red_r", center: p(0, 1, 0), radius: 1)

        alignedBox(material: "green_r", p: p(2, 0, -30), q: p(2+0.25, 0+3, 10))
        alignedBox(material: "blue_r", p: p(-2-0.25, 0, -30), q: p(-2, 0+3, 10))

    }
}
