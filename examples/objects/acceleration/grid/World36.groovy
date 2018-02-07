import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D

def sp1 = builder.sphere(center: Point3D.ORIGIN, radius: 0.25)

int NUM = 10

builder.world(id: "World36") {

    camera(d: 200, lookAt: p(NUM/2, 1, NUM/2), numThreads: 4)
    
    ambientLight(color: Color.WHITE, ls: 0.5f)

    lights {
        pointLight(location: p(NUM/2, 10, NUM/2), ls: 1.0f)
    }

    materials {
        matte(id: "sky", cd: c(0.4, 0.7, 1.0), ka: 1.0, kd: 0.9)
        phong(id: "grey", ks: 0.4, cd: c(0.4), ka: 0.25, kd: 0.6, exp: 10)
        phong(id: "r", ks: 1.0, cd: Color.RED, ka: 1.0, kd: 1.0, exp: 10)
        phong(id: "g", ks: 1.0, cd: Color.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        phong(id: "b", ks: 1.0, cd: Color.BLUE, ka: 1.0, kd: 1.0, exp: 20)
        reflective(id: "rr", ks: 1.0, cd: Color.RED, ka: 1.0, kd: 1.0, exp: 10)
        reflective(id: "gr", ks: 1.0, cd: Color.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        reflective(id: "br", ks: 1.0, cd: Color.BLUE, ka: 1.0, kd: 1.0, exp: 20)
    }

    objects {
        plane(material: "grey", point: p(0, -1, 0), normal: n(0, 1, 0))
        plane(material: "sky", point: p(0, 100, 0), normal: n(0, -1, 0))

        for (int i=0; i<NUM; i++) {
            for (int j=0; j<NUM; j++) {
                instance(material: "rr", object: sp1) {
                    translate(v(i, 0, j));
                }
            }
        }

        for (int i=0; i<NUM; i++) {
            for (int j=0; j<NUM; j++) {
                instance(material: "gr", object: sp1) {
                    translate(v(i, 1, j));
                }
            }
        }

        for (int i=0; i<NUM; i++) {
            for (int j=0; j<NUM; j++) {
                instance(material: "br", object: sp1) {
                    translate(v(i, 2, j));
                }
            }
        }

    }

}
