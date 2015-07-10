import net.dinkla.raytracer.math.Point3DF
import net.dinkla.raytracer.colors.RGBColor

def sp1 = builder.sphere(center: Point3DF.ORIGIN, radius: 1.0)

float v = 0.5

builder.world(id: "World70") {

    viewPlane(maxDepth: 10)

//    camera(d: 2000, eye: p(-4, 1, 20), lookAt: p(3, -0.2, 1), numThreads: 30)
//    camera(d: 500, eye: p(3, 1, 0), lookAt: p(-1, -1, -1), numThreads: 30)
    camera(d: 2000, eye: p(-4, 3, 20), lookAt: p(3, 1, 1), numThreads: 30)

    ambientLight(ls: 0.5f)

    lights {
        pointLight(location: p(0, 10, 5), ls: 1.0f)
    }

    materials {
        matte(id: "sky", cd: c(0.3, 0.6, 1.0), ka: 1.0, kd: 0.9)
        phong(id: "grey", ks: 0.4, cd: c(0.4), ka: 0.25, kd: 0.6, exp: 10)
        phong(id: "r", ks: 1.0, cd: RGBColor.RED, ka: 1.0, kd: 1.0, exp: 10)
        phong(id: "g", ks: 1.0, cd: RGBColor.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        phong(id: "b", ks: 1.0, cd: RGBColor.BLUE, ka: 1.0, kd: 1.0, exp: 20)
        reflective(id: "rr", ks: 1.0, cd: RGBColor.RED, ka: 1.0, kd: 1.0, exp: 10)
        reflective(id: "gr", ks: 1.0, cd: RGBColor.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        reflective(id: "br", ks: 1.0, cd: RGBColor.BLUE, ka: 1.0, kd: 1.0, exp: 20)
        transparent(id: "rt", ks: 0.1, ka: 0.1, kd: 0.2, kt: 0.5, kr: 0.5, exp: 10, ior: 1.02, cd: RGBColor.RED, cs: RGBColor.WHITE, cr: RGBColor.WHITE)
        transparent(id: "gt", ks: 0.1, ka: 0.1, kd: 0.4, kt: 0.5, kr: 0.5, exp: 10, ior: 1.02, cd: RGBColor.GREEN, cs: RGBColor.WHITE, cr: RGBColor.WHITE)
        transparent(id: "bt", ks: 0.1, ka: 0.1, kd: 0.4, kt: 0.5, kr: 0.5, exp: 10, ior: 1.02, cd: RGBColor.BLUE, cs: RGBColor.WHITE, cr: RGBColor.WHITE)
        transparent(id: "trans", ks: 0.0, ka: 0.0, kd: 0.0, kt: 0.5, kr: 0.5, exp: 10, ior: 1.0, cd: RGBColor.WHITE, cs: RGBColor.WHITE, cr: RGBColor.WHITE)
        transparent(id: "trans2", ks: 1.0, ka: 0.0, kd: 1.0, kt: 0.5, kr: 0.5, exp: 10, ior: 1.01, cd: RGBColor.WHITE, cs: RGBColor.WHITE, cr: RGBColor.WHITE)
    }

    objects {
        plane(material: "r")
        plane(material: "b", point: p(0, 100, 0), normal: n(0, -1, 0))

        //sphere(material: "rt", center: p(1, 1, 0), radius: 1)
        sphere(material: "trans2", center: p(3, 1, 0), radius: 1)
        //sphere(material: "bt", center: p(5, 1, 0), radius: 1)
//        sphere(material: "trans", center: p(3, 1, 0), radius: 0.2)

        alignedBox(p: p(-1.25, 0, -2.25), q: p(-1, 2, -2), material: "r")
        alignedBox(p: p(7, 0, -2.25), q: p(7.25, 2, -2), material: "g")
        alignedBox(p: p(3, 0, -2.25), q: p(3.25, 2, -2), material: "b")

        alignedBox(p: p(-1.25, 0, 2), q: p(-1, 2, 2.25), material: "r")
        alignedBox(p: p(7, 0, 2), q: p(7.25, 2, 2.25), material: "g")
        alignedBox(p: p(3, 0, 2), q: p(3.25, 2, 2.25), material: "b")

        alignedBox(p: p(3-v, 1-v, 0-v), q: p(3+v, 1+v, 0+v), material: "b")

    }

}
