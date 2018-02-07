import net.dinkla.raytracer.colors.Color

boolean hasShadows = true

int NUM = 25

int column = NUM/2 - 0.15

def r = new Random()

//TODO: Camera von oben sieht nichts. Warum?

builder.world(id: "World39") {

    camera(d: 500, eye: p(column, column+1, NUM*2), lookAt: p(column, column, NUM/2))

    ambientLight(color: Color.WHITE, ls: 0.5f)

    lights {
        pointLight(location: p(NUM / 2, 10, NUM / 2), ls: 1.0f, shadows: hasShadows)
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
        for (int i = 0; i < NUM; i++) {
            for (int j = 0; j < NUM; j++) {
                for (int k = 0; k < NUM; k++) {
                    int exp = (int) (r.nextFloat() * 50)
                    float ks = r.nextFloat()
                    def col = c(r.nextFloat(), r.nextFloat(), r.nextFloat())
                    float ka = 0.2 + r.nextFloat() * 0.8
                    matte(id: "c$i-$j-$k", ks: 1.0, cd: col, ka: 1.0, kd: 1.0, exp: exp)
                }
            }
        }
    }

    objects {
        plane(material: "grey", point: p(0, -1, 0), normal: n(0, 1, 0))
        plane(material: "sky", point: p(0, 100, 0), normal: n(0, -1, 0))

//        sphere(center: p(0, 0, 0), radius: 0.25, material: "r")

        grid {
            for (int i = 0; i < NUM; i++) {
                for (int j = 0; j < NUM; j++) {
                    for (int k = 0; k < NUM; k++) {
                        sphere(center: p(i, j, k), radius: 0.25, material: "c$i-$j-$k")
                    }
                }
            }
        }

    }

}
