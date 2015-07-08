// Drei Pferde in rot, grün, blau

import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.objects.Instance

def ply1 = builder.ply(file: "/opt/rendering/ply/horse/Horse97K.ply", smooth: true)

def r = new Random()

Instance inst1 = builder.instance(object: ply1) {
    scale(v(1, 1, 1))
}

final int NUM_ITEMS = 3

builder.world(id: "World49") {

    viewPlane(resolution: Resolution.RESOLUTION_480, maxDepth: 2)

    camera(d: 800, eye: p(0, 1, 3), lookAt: p(1.3, 0, 0), numThreads: 8)

    ambientLight(color: RGBColor.WHITE, ls: 0.5f)

    lights {
        pointLight(location: p(-1, 2, 3), color: c(1, 1, 1), ls: 1)
    }

    materials {
        phong(id: "grey", ks: 1.0, cd: c(0.1, 0.1, 0.1), ka: 0.5, kd: 1.0, exp: 10)
        matte(id: "sky", cd: c(0.1, 0.7, 1.0), ka: 0.75, kd: 1.0)
        reflective(id: "white", ks: 0.7, cd: c(1.0, 1.0, 1.0), ka: 0.5, kd: 0.75, exp: 2)
        phong(id: "red", ks: 0.7, cd: c(0.95, 0.2, 0.05), ka: 0.5, kd: 0.7, exp: 10)
        phong(id: "green", ks: 0.7, cd: c(0.2, 0.95, 0.05), ka: 0.5, kd: 0.7, exp: 20)
        phong(id: "blue", ks: 0.7, cd: c(0.2, 0.05, 0.95), ka: 0.5, kd: 0.7, exp: 30)
    }

    objects {
        plane(point: p(0,0,0), normal: n(0, 1, 0), material: "white")

        instance(object: inst1, material: "red") {
            //rotateY((int) (r.nextFloat()*360))
            translate(v(0, 0, 0))
        }        

        instance(object: inst1, material: "green") {
            rotateY(10)
            translate(v(1, 0, 0))
        }

        instance(object: inst1, material: "blue") {
            rotateY(-5)
            translate(v(2, 0, 0))
        }

    }

}