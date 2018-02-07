import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.tracers.AreaLighting
import net.dinkla.raytracer.samplers.MultiJittered
import net.dinkla.raytracer.samplers.Sampler
import net.dinkla.raytracer.cameras.lenses.FishEye

Vector3D vecW = new Vector3D(1, 0, 0)
Vector3D vecH = new Vector3D(0, 2, 0)
Vector3D vecD = new Vector3D(0, 0, 1)

def sampler1 = new Sampler(new MultiJittered(), 2500, 100);
//def sampler1 = new Sampler(new PureRandom(), 2500, 100);
sampler1.mapSamplesToUnitDisk()

def r1

int numSamples = 16

builder.world(id: "World23") {

    tracer(type: AreaLighting)

//    camera(d: 500, eye: p(0, 1.5, 10), lookAt: p(0, 1, 0), numThreads: 20)
    //camera(d: 500, eye: p(0, 1.5, 10), lookAt: p(0, 1, 0), type: FishEye, maxPsi: 180)
    camera(eye: p(0, 1.5, 10), lookAt: p(0, 1, 0), type: FishEye, maxPsi: 180)

    ambientLight(color: Color.WHITE, ls: 0.5)

    materials {
        phong(id: "m1", cd: c(0.1, 0.8, 0.2), ka: 0.25, kd: 0.75, ks: 0.8)
//        phong(id: "m2", cd: c(0.4), ka: 0.25, kd: 0.75, ks: 1.0)
        phong(id: "m2", cd: c(1.0, 1.0, 1.0), ka: 1.0, kd: 1.0, ks: 1.0)
//        emissive(id: "em", ce: c(1.0, 0.9, 0.2), le: 0.2)
        emissive(id: "em", ce: c(1.0, 0.9, 0.2), le: 1.0)
    }

    objects {
        box(p0: p(-6, 0, 1), a: vecW, b: vecH, c: vecD, material: "m1")
        box(p0: p(-2, 0, 1), a: vecW, b: vecH, c: vecD, material: "m1")
        box(p0: p(2, 0, 1), a: vecW, b: vecH, c: vecD, material: "m1")
        box(p0: p(6, 0, 1), a: vecW, b: vecH, c: vecD, material: "m1")
        plane(material: "m2", point: Point3D.ORIGIN, normal: Normal.UP)
        r1 = rectangleLight(p0: p(-10.75f, 2, -10), a: vecW.mult(15.5f), b: vecH.mult(2), sampler: sampler1, material: "em")
//        r1 = disk(center: p(0, 5, -10), radius: 2, normal: n(0, 0, 1), sampler: sampler1, material: "em")
    }

    lights {
        areaLight(object: r1, numSamples: numSamples)
    }

}
