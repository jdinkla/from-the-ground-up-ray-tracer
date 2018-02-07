import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D

def sp1 = builder.sphere(center: Point3D.ORIGIN, radius: 1.0)

builder.world(id: "World70") {

    viewPlane(maxDepth: 2)
    
//    camera(d: 2000, eye: p(-4, 1, 20), lookAt: p(3, -0.2, 1), numThreads: 30)
    camera(d: 2000, eye: p(-4, 3, 20), lookAt: p(3, 1, 1), numThreads: 32)

    ambientLight(ls: 0.5)

    lights {
        pointLight(location: p(0, 10, 5), ls: 1.0)
    }

    materials {
        matte(id: "sky", cd: c(0.3, 0.6, 1.0), ka: 1.0, kd: 0.9)
        phong(id: "grey", ks: 0.4, cd: c(0.4), ka: 0.25, kd: 0.6, exp: 10)
        phong(id: "r", ks: 1.0, cd: Color.RED, ka: 1.0, kd: 1.0, exp: 10)
        phong(id: "g", ks: 1.0, cd: Color.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        phong(id: "b", ks: 1.0, cd: Color.BLUE, ka: 1.0, kd: 1.0, exp: 20)
        reflective(id: "rr", ks: 1.0, cd: Color.RED, ka: 1.0, kd: 1.0, exp: 10)
        reflective(id: "gr", ks: 1.0, cd: Color.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        reflective(id: "br", ks: 1.0, cd: Color.BLUE, ka: 1.0, kd: 1.0, exp: 20)
        transparent(id: "rt", ks: 0.1, ka: 0.1, kd: 0.2, kt: 0.5, kr: 0.5, exp: 10, ior: 1.02, cd: Color.RED, cs: Color.WHITE, cr: Color.WHITE)
        transparent(id: "gt", ks: 0.1, ka: 0.1, kd: 0.4, kt: 0.5, kr: 0.5, exp: 10, ior: 1.02, cd: Color.GREEN, cs: Color.WHITE, cr: Color.WHITE)
        transparent(id: "bt", ks: 0.1, ka: 0.1, kd: 0.4, kt: 0.5, kr: 0.5, exp: 10, ior: 1.02, cd: Color.BLUE, cs: Color.WHITE, cr: Color.WHITE)
        transparent(id: "trans", ks: 0.0, ka: 0.0, kd: 0.0, kt: 0.5, kr: 0.5, exp: 10, ior: 1.0, cd: Color.WHITE, cs: Color.WHITE, cr: Color.WHITE)
        transparent(id: "trans2", ks: 1.0, ka: 0.0, kd: 1.0, kt: 0.5, kr: 0.5, exp: 10, ior: 1.06, cd: Color.WHITE, cs: Color.WHITE, cr: Color.WHITE)
    }

    objects {
//        plane(material: "grey")
//        plane(material: "sky", point: p(0, 100, 0), normal: n(0, -1, 0))

        //sphere(material: "rt", center: p(1, 1, 0), radius: 1)
        sphere(material: "trans2", center: p(3, 1, 0), radius: 1)
        //sphere(material: "bt", center: p(5, 1, 0), radius: 1)

        alignedBox(p: p(-1.25, 0, -2.25), q: p(-1, 2, -2), material: "r")
        alignedBox(p: p(7, 0, -2.25), q: p(7.25, 2, -2), material: "g")
        alignedBox(p: p(3, 0, -2.25), q: p(3.25, 2, -2), material: "b")

        alignedBox(p: p(-1.25, 0, 2), q: p(-1, 2, 2.25), material: "r")
        alignedBox(p: p(7, 0, 2), q: p(7.25, 2, 2.25), material: "g")
        alignedBox(p: p(3, 0, 2), q: p(3.25, 2, 2.25), material: "b")

    }

}
