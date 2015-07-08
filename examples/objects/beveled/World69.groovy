
def r = new java.util.Random()

builder.world(id: "World64") {

    camera(d: 1500, eye: p(0, 5, 10), lookAt: p(5, 0, 0))

    ambientLight(ls: 0.5)

    lights {
        pointLight(location: p(0, 10, 5), ls: 1)
    }

    materials {
        matte(id: "gray", cd: c(1), ka: 0.25, kd: 0.75)
//        reflective(id: "mirror", cd: c("0000FF"), ka: 0.0, kd: 1.0, ks: 1.0, kr: 1.0, cr: c(1, 0, 1.0))
        reflective(id: "mirror", cd: c("0000FF"), ka: 0.0, kd: 0.8, ks: 0.3, kr: 0.4, cr: c(1, 0, 1.0))
        phong(id: "m1", cd: c("adff2f"), ka: 0.5, kd: 0.75, ks: 0.55, exp: 2, ior: 1.02)
        phong(id: "m2", cd: c("ffa07a"), ka: 0.5, kd: 0.75, ks: 0.55, exp: 15, ior: 1.02)
        phong(id: "m3", cd: c("ffc0cb"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 9, ior: 1.02)
        phong(id: "m4", cd: c("FFD700"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 5, ior: 1.02)
        phong(id: "m5", cd: c("EEC900"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 5, ior: 1.02)
    }

    objects {
        plane(material: "gray")

        grid {
            for (int i=0; i<50; i++) {
                def x = r.nextFloat() * (i % 20) 
                def y = 0
                def z = r.nextFloat() * -1 * (i % 20)
                def x2 = x + r.nextFloat()
                def y2 = y + r.nextFloat()
                def z2 = z + r.nextFloat()
                def mat = "m" + String.valueOf((i % 5) + 1)
                beveledBox(p0: p(x, y, z), p1: p(x2, y2, z2), rb: 0.01, material: mat, wireFrame: false)
            }
        }
    }

}
