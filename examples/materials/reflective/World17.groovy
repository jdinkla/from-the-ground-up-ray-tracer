import net.dinkla.raytracer.math.Point3DF
import net.dinkla.raytracer.math.Normal

def o1 = builder.openCylinder(y0: 0, y1: 3.5f, radius: 0.9f)
def s1 = builder.solidCylinder(y0: 0, y1: 3.5f, radius: 0.9f)
def a1 = builder.alignedBox(p: p(2, 0, -2), q: p(2.1, 3.3, 5))

builder.world(id: "World17") {

    viewPlane(numSamples: 0, maxDepth: 5)
    //camera(d: 800, eye: p(0, 2.5, 7), lookAt: p(1, 0.5, 1))
//    camera(d: 800, eye: p(0, 2.5, 15), lookAt: p(1.5, 0.5, 1))
//    camera(d: 800, eye: p(0, 12.5, 15), lookAt: p(1.5, 0.5, 1))
//    camera(d: 800, eye: p(0, 1.1, 15), lookAt: p(1.5, 0.5, 1), numThreads: 30)
//    camera(d: 800, eye: p(0, 250, 15), lookAt: p(1.5, 0.5, 1), type: PinholePar, numThreads: 30)
    camera(d: 800, eye: p(0, 2.1, 15), lookAt: p(1.5, 0.5, 1), numThreads: 16)

    ambientLight(ls: 0.5)

    lights {
        pointLight(location: p(-6, 1, 2), ls: 1)
        pointLight(location: p(4, 2, 7), ls: 1, color: c(1.0, 0.9, 0.9))
    }

    materials {
        reflective(id: "m2", cd: c(1), ka: 0.75f, kd: 0.75f, kr: 1)
        reflective(id: "m1", cd: c(1, 1, 0), ka: 0.75f, kd: 0.75f, exp: 10, ks: 1.0f, kr: 1)
        reflective(id: "p1", cd: c(1, 1, 0), ka: 0.75f, kd: 0.75f, exp: 10, ks: 1.0f, kr: 1)
        reflective(id: "m3", cd: c(0, 0, 1), ka: 0.25f, kd: 0.5f, kr: 1)
        reflective(id: "p2", cd: c(1, 0, 1), ka: 0.75f, kd: 0.75f, exp: 10, ks: 1.0f, kr: 1)
/*
        phong(id: "m2", cd: c(1), ka: 0.75f, kd: 0.75f, kr: 1)
        phong(id: "m1", cd: c(1, 1, 0), ka: 0.75f, kd: 0.75f, exp: 10, ks: 1.0f, kr: 1)
        phong(id: "p1", cd: c(1, 1, 0), ka: 0.75f, kd: 0.75f, exp: 10, ks: 1.0f, kr: 1)
        phong(id: "m3", cd: c(0, 0, 1), ka: 0.25f, kd: 0.5f, kr: 1)
        phong(id: "p2", cd: c(1, 0, 1), ka: 0.75f, kd: 0.75f, exp: 10, ks: 1.0f, kr: 1)
*/
    }

    objects {
        plane(material: "m2", point: Point3DF.ORIGIN, normal: Normal.UP)
        solidCylinder(material: "m1", y0: 0, y1: 3.5f, radius: 0.9f)
        alignedBox(material: "m3", p: p(2, 0, -2), q: p(3, 2.3, 3))

        instance(material: "m3", object: a1) {
            translate(v(2, 0, 0))
        }

        instance(material: "m1", object: s1) {
            translate(v(-2, 0, -2))
        }

        instance(material: "m1", object: o1) {
            translate(v(-4, 0, -1))
        }

        sphere(center: p(8, 0.5, 0), radius: 0.5, material: "m1")

        alignedBox(material: "p2", p: p(-4, 0.5, 20), q: p(120, 1.2, 21))

    }

}
