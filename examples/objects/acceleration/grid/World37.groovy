import net.dinkla.raytracer.colors.Color

//def sp1 = builder.sphere(center: Point3D.ORIGIN, radius: 0.25)

int NUM = 100

int column = NUM/2 - 0.15

//TODO: Camera von oben sieht nichts. Warum?
//TODO: Wenn Grid vorher, wird alles ins Grid gesteckt

//builder.world(id: "World37", accelerator: new ListAccelerator()) {
builder.world(id: "World37") {

    camera(d: 500, eye: p(column, 4, NUM*1.5), lookAt: p(column, 1, NUM/2))

    //camera(direction: 1000, eye: p(0, 10, 10), lookAt: p(0, 0, 0))

    ambientLight(color: Color.WHITE, ls: 0.5)

    lights {
        pointLight(location: p(NUM / 2, 10, NUM / 2), ls: 1.0, shadows: false)
    }

    materials {
        matte(id: "sky", cd: c(0.4, 0.7, 1.0), ka: 1.0, kd: 0.9)
        matte(id: "grey", ks: 0.4, cd: c(0.4, 0.4, 0.4), ka: 0.25, kd: 0.6, exp: 10)
        matte(id: "r", ks: 1.0, cd: Color.RED, ka: 1.0, kd: 1.0, exp: 10)
        matte(id: "g", ks: 1.0, cd: Color.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        matte(id: "b", ks: 1.0, cd: Color.BLUE, ka: 1.0, kd: 1.0, exp: 20)
//        reflective(id: "rr", ks: 1.0, cd: RGBColor.RED, ka: 1.0, kd: 1.0, exp: 10)
//        reflective(id: "gr", ks: 1.0, cd: RGBColor.GREEN, ka: 1.0, kd: 1.0, exp: 20)
//        reflective(id: "br", ks: 1.0, cd: RGBColor.BLUE, ka: 1.0, kd: 1.0, exp: 20)
    }

    objects {
        plane(material: "grey", point: p(0, -1, 0), normal: n(0, 1, 0))
        plane(material: "sky", point: p(0, 100, 0), normal: n(0, -1, 0))

//        sphere(center: p(0, 0, 0), radius: 0.25, material: "r")

        grid {
            for (int i = 0; i < NUM; i++) {
                for (int j = 0; j < NUM; j++) {
                    sphere(center: p(i, 0, j), radius: 0.25, material: "r")
                }
            }
//        }

//        grid {
            for (int i = 0; i < NUM; i++) {
                for (int j = 0; j < NUM; j++) {
                    sphere(center: p(i, 1, j), radius: 0.25, material: "g")
                }
            }
//        }

//        grid {
            for (int i = 0; i < NUM; i++) {
                for (int j = 0; j < NUM; j++) {
                    sphere(center: p(i, 2, j), radius: 0.25, material: "b")
                }
            }
        }


    }

}
