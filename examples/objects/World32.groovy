import net.dinkla.raytracer.colors.Color

def t1 = builder.smoothTriangle(a: p(-0.25, 0, 0.1), b: p(0.25, 0.25, -0.1), c: p(0.25, -0.25, 0), n1: n(0, 1.1, 1.1), n2: n(1.1, 0, -0.5))

builder.world(id: "World31") {

    camera(d: 1000)

    ambientLight(color: Color.WHITE, ls: 0.5)

    lights {
        pointLight(location: p(0, -1, 1.5), ls: 1.0)
        pointLight(location: p(0, 1, 1.5), ls: 1.0)
        pointLight(location: p(0, 0, 1.5), ls: 1.0)
    }

    materials {
        matte(id: "grey", ks: 0.8, cd: c(0.4), ka: 0.25, kd: 0.8, exp: 2)
        phong(id: "r", ks: 1.0, cd: Color.RED, ka: 1.0, kd: 1.0, exp: 10)
        phong(id: "g", ks: 1.0, cd: Color.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        phong(id: "b", ks: 1.0, cd: Color.BLUE, ka: 1.0, kd: 1.0, exp: 20)
        reflective(id: "rr", ks: 1.0, cd: Color.RED, ka: 1.0, kd: 1.0, exp: 10)
        reflective(id: "gr", ks: 1.0, cd: Color.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        reflective(id: "br", ks: 1.0, cd: Color.BLUE, ka: 1.0, kd: 1.0, exp: 20)
    }

    objects {
        plane(material: "grey", point: p(0, 0, -2), normal: n(0, 0, 1))

        instance(material: "r", object: t1) {
        }

        instance(material: "g", object: t1) {
            rotateZ(120)
        }

        instance(material: "b", object: t1) {
            rotateZ(240)
        }

    }

}
