import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D

def sp1 = builder.sphere(center: Point3D.ORIGIN, radius: 1.0)

builder.world(id: "World35") {

    camera(d: 2000, eye: p(-4, 1, 20), lookAt: p(3, -0.2, 1))
    
    ambientLight(color: Color.WHITE, ls: 0.5)

    lights {
        pointLight(location: p(0, 0, 5), ls: 1.0)
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
        // TODO: Energieerhaltung !!
        transparent(id: "rt", ks: 0.1, ka: 0.1, kd: 0.2, kt: 0.5, kr: 0.5, exp: 10, ior: 1.02, cd: Color.RED, cs: Color.WHITE, cr: Color.WHITE)
        transparent(id: "gt", ks: 0.1, ka: 0.1, kd: 0.4, kt: 0.5, kr: 0.5, exp: 10, ior: 1.02, cd: Color.GREEN, cs: Color.WHITE, cr: Color.WHITE)
        transparent(id: "bt", ks: 0.1, ka: 0.1, kd: 0.4, kt: 0.5, kr: 0.5, exp: 10, ior: 1.02, cd: Color.BLUE, cs: Color.WHITE, cr: Color.WHITE)
//        transparent(id: "trans", ks: 0.0, ka: 0.0, kd: 0.0, kt: 0.5, kr: 0.5, exp: 10, ior: 1.5, cd: RGBColor.WHITE, cs: RGBColor.WHITE, cr: RGBColor.WHITE)
//        matte(id: "trans", ks: 0.0, ka: 0.0, kd: 0.0, kt: 0.5, kr: 0.5, exp: 10, ior: 1.5, cd: RGBColor.WHITE, cs: RGBColor.WHITE, cr: RGBColor.WHITE)
        transparent(id: "trans", ks: 0.0, ka: 0.0, kd: 0.0, kt: 0.5, kr: 0.5, exp: 10, ior: 1.5, cd: Color.WHITE, cs: Color.WHITE, cr: Color.WHITE)
    }

    objects {
        plane(material: "grey", point: p(0, -1, 0), normal: n(0, 1, 0))
        plane(material: "sky", point: p(0, 100, 0), normal: n(0, -1, 0))

        sphere(material: "rt", center: p(1, 0, 1), radius: 0.9)
        sphere(material: "gt", center: p(3, 0, 1), radius: 0.9)
        sphere(material: "bt", center: p(5, 0, 1), radius: 0.9)

        instance(material: "trans", object: sp1) {
            scale(v(2, 1.0, 1.0))
//            translate(v(3, 0.5, 2))
            translate(v(3, 0.5, 3))
        }
    }

}
