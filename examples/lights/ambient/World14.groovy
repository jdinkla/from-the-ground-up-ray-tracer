import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.samplers.MultiJittered

def NUM_AMBIENT_SAMPLES = 16

def samp1 = new Sampler(new MultiJittered(), 2500, 50)
samp1.mapSamplesToHemiSphere(3)

builder.world(id: "World14") {

    viewPlane(numSamples: 16)
    
    //camera(d: 1000, eye: p(0, 1, 7), lookAt: p(0, 0.75, 0))
    camera(d: 1000, eye: p(0, 1, 7), lookAt: p(0, 0.75, 0), numThreads: 32)

    ambientOccluder(minAmount: RGBColor.WHITE,
        //sampler: new Sampler(new PureRandom(), 250, 10),
//        sampler: new Sampler(new Hammersley(), 2500, 10),
//        sampler: new Sampler(new Jittered(), 2500, 10),
//          sampler: new Sampler(new Regular(), 2500, 10),
//        sampler: new Sampler(new Constant(), 2500, 10),
        sampler: samp1,
        numSamples: NUM_AMBIENT_SAMPLES)

    lights {
    }

    materials {
        matte(id: "m1", cd: c(1, 1, 0), ka: 0.75f, kd: 0)
        matte(id: "m2", cd: c(1), ka: 0.75f, kd: 0)
    }

    objects {
        sphere(material: "m1", center: p(0, 1, 0), radius: 1)
        plane(material: "m2", point: Point3D.ORIGIN, normal: Normal.UP)
    }

}
