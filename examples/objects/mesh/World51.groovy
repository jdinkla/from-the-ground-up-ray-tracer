
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.objects.Instance

//Grid ply1 = builder.ply(file: "examples/ply/Bunny16K.ply", multiplier: 2.0, smooth: true)
//Grid ply1 = builder.ply(file: "examples/ply/Bunny69K.ply", multiplier: 2.0)
//Grid ply1 = builder.ply(file: "/Users/jorndinkla/Downloads/rendering/ply-examples/dragon.ply", multiplier: 2.0)

def ply1 = builder.ply(file: "/opt/rendering/ply/horse/Horse97K.ply", multiplier: 2.0, smooth: true)

def r = new Random()

Instance inst1 = builder.instance(object: ply1) {
    scale(v(5, 5, 5))
}

final int NUM_ITEMS = 50

builder.world(id: "World51") {

    viewPlane(resolution: Resolution.RESOLUTION_480, maxDepth: 5)

//    camera(d: 800, eye: p(1, 0.3, 3), lookAt: p(0.4, 0.5, 0), numThreads: 8)
    camera(d: 1200, eye: p(1, 0.3, 3), lookAt: p(0.4, 0.25, 0), numThreads: 8)

    ambientLight(ls: 0.5)

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
        reflective(id: "blueT", ks: 0.7, cd: c(0.2, 0.05, 0.95), ka: 0.5, kd: 0.7, exp: 30, ior: 1.03)
    }

    objects {
        plane(point: p(0,0,0), normal: n(0, 1, 0), material: "white")

        grid {
            for (int i=0; i < NUM_ITEMS; i++) {
                instance(object: ply1, material: "blueT") {
                    // scale(v(1.1, 1.1, 1.1))
                    translate(v(0, 0, -i))
                }
            }
        }

    }

}