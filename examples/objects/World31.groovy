import net.dinkla.raytracer.colors.Color

def t1 = builder.smoothTriangle(a: p(-0.25, 0, 0.2), b: p(0.25, 0.1, -0.1), c: p(0.23, -0.2, +0.025))
def t2 = builder.smoothTriangle(a: p(0, -1, 0), b: p(0.75, 0.4, -0.025), c: p(-0.05, 0, +0.025))

def tr1 = builder.triangle(a: p(3, 0, 0.2), b: p(3.2, 0.2, -0.2), c: p(2.9, -0.3, +0.025))
def tr2 = builder.triangle(a: p(3.1, 0, 0), b: p(3.15, 0.3, -0.21), c: p(2.8, -0.33, +0.125))

builder.world(id: "World31") {

    camera(d: 250, lookAt: p(2,0,0), eye: p(0, 0.1, 2))
    
    ambientLight(color: Color.WHITE, ls: 0.5f)

    lights {
        pointLight(location: p(0, 10, 0), ls: 1.0f)
//        pointLight(location: p(0, 9, 0.5), ls: 1.0f)
    }

    materials {
        matte(id: "grey", ks: 0.5, cd: c(0.4), ka: 0.25, kd: 0.75, exp: 50)
        matte(id: "rm", ks: 1.0, cd: Color.RED, ka: 1.0, kd: 1.0, exp: 10)
        matte(id: "gm", ks: 1.0, cd: Color.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        matte(id: "bm", ks: 1.0, cd: Color.BLUE, ka: 1.0, kd: 1.0, exp: 20)
        phong(id: "r", ks: 1.0, cd: Color.RED, ka: 1.0, kd: 1.0, exp: 10)
        phong(id: "g", ks: 1.0, cd: Color.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        phong(id: "b", ks: 1.0, cd: Color.BLUE, ka: 1.0, kd: 1.0, exp: 20)
        reflective(id: "rr", ks: 1.0, cd: Color.RED, ka: 1.0, kd: 1.0, exp: 10)
        reflective(id: "gr", ks: 1.0, cd: Color.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        reflective(id: "br", ks: 1.0, cd: Color.BLUE, ka: 1.0, kd: 1.0, exp: 20)
    }

    objects {
        plane(material: "grey", point: p(0, 0, -2), normal: n(0, 0, 1))

        //sphere(material: "b", center: p(3,0,0), radius: 0.1)
        //sphere(material: "r", center: p(3.09,0.1,0), radius: 0.1)
        //sphere(material: "g", center: p(3.04,0.1,-0.09), radius: 0.1)

        instance(material: "bm", object: tr1) {
            rotateX(10)
        }

        instance(material: "rm", object: tr2) {
            rotateX(20)
        }

//        instance(material: "bm", object: tr1) {
//            rotateX(10)
//        }

//        instance(material: "rm", object: tr2) {
//            rotateX(20)
//        }

        //triangle(a: p(3, 0, 0.2), b: p(3.2, 0.2, -0.2), c: p(2.9, -0.3, +0.025), material: "bm")
        //triangle(a: p(3.1, 0, 0), b: p(3.15, 0.3, -0.21), c: p(2.8, -0.33, +0.125), material: "rm")

        instance(material: "r", object: t1) {
        }

        instance(material: "g", object: t1) {
            rotateZ(120)
        }

        instance(material: "b", object: t1) {
            rotateZ(240)
        }

        instance(material: "r", object: t2) {
            translate(v(2, 0, 0))
        }

        instance(material: "g", object: t2) {
            rotateZ(20)
            translate(v(2, 0, 0))
        }

        instance(material: "b", object: t2) {
            rotateZ(40)
            translate(v(2, 0, 0))
        }

//        instance(material: "rm", object: t2) {
//            translate(v(2, 0, 0))
//        }

//        instance(material: "gm", object: t2) {
//            rotateZ(20)
//            translate(v(2, 0, 0))
//        }

//        instance(material: "bm", object: t2) {
//            rotateZ(40)
//            translate(v(2, 0, 0))
//        }

    }

}
