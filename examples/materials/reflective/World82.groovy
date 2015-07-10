import net.dinkla.raytracer.math.Point3DF
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.tracers.AreaLighting

def NUM_AMBIENT_SAMPLES = 1
Sampler sampler1 = new Sampler(new MultiJittered(), 2500, 10)
sampler1.mapSamplesToHemiSphere(1.0)

def b1 = builder.alignedBox(p: p(0, 0, 0), q: p(5, 1.75f, 0.15f))
def s1 = builder.solidCylinder(y0: 0, y1: 2.5, radius: 0.5)

int numProcs = Runtime.getRuntime().availableProcessors();

def r1

builder.world(id: "World82") {

    tracer(type: AreaLighting)

    camera( numThreads: numProcs * 3,
            //ray: SampledRenderer, raySampler: sampler1, rayNumSamples: 0,
            d: 1250, eye: p(0, 1, -10), lookAt: p(-0.5, 1.2, 0))

    ambientLight(ls: 0.5f)

    ambientOccluder(sampler: sampler1, numSamples: NUM_AMBIENT_SAMPLES)
    
    lights {
        pointLight(location: p(10, 7, 0), ls: 1.0f)
// TODO        environment()
    }

    materials {
        phong(id: "m1", ks: 1.0, cd: c(1, 1, 1), ka: 0.25, kd: 0.9, exp: 1)
        phong(id: "m2", ks: 0.5, cd: c(0.1, 0.7, 0.3), ka: 0.25, kd: 0.75, exp: 10)
/*
        glossy(id: "m3", ks: 0.5, cd: c(1, 1, 0), ka: 0.25, kd: 0.75, exp: 50)
        glossy(id: "m4", ks: 0.1, cd: c(1, 0, 0), ka: 0.25, kd: 0.75, exp: 3)
        glossy(id: "m5", ks: 0.5, cd: c(0, 0.5, 1), ka: 0.25, kd: 0.75, exp: 10)
         */
        glossy(id: "m3", ks: 0.5, cd: c(1, 1, 0), ka: 0.25, kd: 0.75, exp: 1, kr: 1, sampler: sampler1)
        glossy(id: "m4", ks: 0.1, cd: c(1, 0, 0), ka: 0.25, kd: 0.75, exp: 2, kr: 1, sampler: sampler1)
        glossy(id: "m5", ks: 0.5, cd: c(0, 0.5, 1), ka: 0.25, kd: 0.75, exp: 3, kr: 1, sampler: sampler1)
        matte(id: "black", cd: c(0, 0, 0))
        emissive(id: "emissive", ce: c(1.0, 1.0, 1.0), le: 1.0)
        
    }

    objects {
         plane(material: "m1", point: Point3DF.ORIGIN, normal: Normal.UP)
//         plane(material: "black", point: p(0, 0, -500), normal: Normal.BACK)
         instance(material: "m2", object: b1) {
            rotateY(25)
            translate(v(-3, 0, 2))
         }
         sphere(material: "m3", center: p(0.5, 0.6, -1), radius: 0.6)
         sphere(material: "m4", center: p(-1.5, 0.4, -1.8), radius: 0.4)
         instance(material: "m5", object: s1) {
            translate(v(-0.7, 0, -1))
         }

        r1 = rectangleLight(p0: p(-1000, 1000, -1000), a: v(2000, 0, 0), b: v(0, 0, 2000), sampler: sampler1, material: "emissive")

    }

    lights {
        areaLight(object: r1, numSamples: 16)
    }

}
