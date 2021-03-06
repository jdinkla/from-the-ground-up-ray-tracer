package materials.reflective

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.utilities.Resolution

def b1 = builder.alignedBox(p: p(0, 0, 0), q: p(5, 1.75f, 0.15f))
def s1 = builder.solidCylinder(y0: 0, y1: 2.5, radius: 0.5)

int numProcs = Runtime.getRuntime().availableProcessors();

builder.world(id: "World27c") {

    viewPlane(resolution: new Resolution(1440))

    camera( numThreads: numProcs * 3,
            d: 1750, eye: p(0, 3.2, -10), lookAt: p(-0.5, 1.2, 0))
    
    ambientLight(ls: 0.55)

    lights {
        pointLight(location: p(10, 7, 0), ls: 1.0)
    }

    materials {
    /*
        phong(id: "m1", ks: 1.0, cd: c(1, 1, 1), ka: 0.25, kd: 0.9, exp: 1)
        phong(id: "m2", ks: 0.5, cd: c(0.1, 0.7, 0.3), ka: 0.25, kd: 0.75, exp: 10)
        phong(id: "m3", ks: 0.5, cd: c(1, 1, 0), ka: 0.25, kd: 0.75, exp: 50)
        phong(id: "m4", ks: 0.1, cd: c(1, 0, 0), ka: 0.25, kd: 0.75, exp: 3)
        phong(id: "m5", ks: 0.5, cd: c(0, 0.5, 1), ka: 0.25, kd: 0.75, exp: 10)
        */
        reflective(id: "m1", ks: 1.0, cd: c(1, 1, 1), ka: 0.25, kd: 0.9, exp: 1)
        reflective(id: "m2", ks: 0.5, cd: c(0.1, 0.7, 0.3), ka: 0.25, kd: 0.75, exp: 10)
        reflective(id: "m3", ks: 0.5, cd: c(1, 1, 0), ka: 0.25, kd: 0.75, exp: 50)
        reflective(id: "m4", ks: 0.1, cd: c(1, 0, 0), ka: 0.25, kd: 0.75, exp: 3)
        reflective(id: "m5", ks: 0.5, cd: c(0, 0.5, 1), ka: 0.25, kd: 0.75, exp: 10)
        matte(id: "black", cd: c(0, 0, 0))
    }

    objects {
         plane(material: "m1", point: Point3D.ORIGIN, normal: Normal.UP)
//         plane(material: "black", point: p(0, 0, -500), normal: Normal.BACK)
         instance(material: "m2", object: b1) {
            rotateY(-20)
            translate(v(-3f, 0, 0))            
         }
         sphere(material: "m3", center: p(0.5, 0.6, -1), radius: 0.6)
         sphere(material: "m4", center: p(-1.5, 0.4, 0.5), radius: 0.4)
         instance(material: "m5", object: s1) {
            translate(v(-0.7, 0, -1))
         }
    }

}
