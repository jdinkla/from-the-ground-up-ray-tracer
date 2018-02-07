import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

//def sp1 = builder.sphere(center: Point3D.ORIGIN, radius: 0.25)

boolean hasShadows = true

int NUM = 10

int column = NUM/2 - 0.15

def r = new Random()

//TODO: Camera von oben sieht nichts. Warum?

builder.world(id: "World40") {

    viewPlane(resolution: Resolution.RESOLUTION_1080, maxDepth: 10)
    
//    camera(d: 1000, eye: p(-NUM, column+1, NUM*2), lookAt: p(column, column, NUM/2), type: IterativePinhole)
//    camera(d: 1000, eye: p(-5, NUM + 5, NUM + 5), lookAt: p(column, NUM - 5, column), type: IterativePinhole)
//    camera(d: 1000, eye: p(-5, NUM + 5, NUM + 5), lookAt: p(column, NUM - 10, column), type: IterativePinhole)
//    camera(d: 1000, eye: p(-5, NUM + 5, NUM + 5), lookAt: p(column, NUM - NUM/5, column), type: PinholePar)
    camera(d: 1000, eye: p(-1, NUM, NUM + 5), lookAt: p(column, NUM - NUM/5, column), numThreads: 4)

    ambientLight(color: Color.WHITE, ls: 0.5f)

    lights {
        pointLight(location: p(NUM, NUM*2, NUM*2), ls: 1.0f, shadows: hasShadows)
    }

    materials {
        reflective(id: "grey", ks: 1.0, cd: c(0.1, 0.1, 0.1), ka: 0.0, kd: 1.0, exp: 10)
        for (int i = 0; i < NUM; i++) {
            for (int j = 0; j < NUM; j++) {
                for (int k = 0; k < NUM; k++) {
                    int exp = (int) (r.nextFloat() * 50)
                    float ks = r.nextFloat() / 40
                    def col = c(r.nextFloat(), r.nextFloat(), r.nextFloat())
                    float ka = 0.0 // 0.2 + r.nextFloat() * 0.8
                    float kr = r.nextFloat() / 40
                    float kd = 1.0
                    //phong(id: "c$i-$j-$k", ks: 1.0, cd: col, ka: 1.0, kd: 1.0, exp: exp)
//                    reflective(id: "c$i-$j-$k", ks: 1.0, cd: col, ka: 1.0, kd: 1.0, exp: exp, kr: kr)
//                    reflective(id: "c$i-$j-$k", ks: ks, cd: col, ka: ka, kd: kd, exp: exp, kr: kr)
                    reflective(id: "c$i-$j-$k", ks: ks, cd: col, cr: col, cs: col, ca: col, ka: ka, kd: kd, exp: exp, kr: kr)
                }
            }
        }
    }

    objects {
        plane(material: "grey", point: p(0, 0, -5), normal: n(0, 0, 1))    // hinten
        plane(material: "grey", point: p(0, -5, 0), normal: n(0, 1, 0))    // unten
        plane(material: "grey", point: p(NUM+5, 0, 0), normal: n(-1, 0, 0))    // rechts hinten
        plane(material: "grey", point: p(0, NUM*2+5, 0), normal: n(0, -1, 0)) // oben

        plane(material: "grey", point: p(0, 0, NUM*2+5), normal: n(0, 0, -1)) // rechts vorne
        plane(material: "grey", point: p(-NUM - 5, 0,  0), normal: n(1, 0, 0)) // links

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
