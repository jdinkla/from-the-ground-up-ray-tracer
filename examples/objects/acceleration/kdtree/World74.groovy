import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree

String path = '/opt/rendering/ply'

def ply1 = builder.ply(file: "${path}/bunny/Bunny4K.ply", multiplier: 2.0, smooth: true, type: Grid)
def ply2 = builder.ply(file: "${path}/bunny/Bunny4K.ply", smooth: true, type: KDTree)

def r = new Random()

builder.world(id: "World74") {

    viewPlane(resolution: Resolution.RESOLUTION_480, maxDepth: 2, numSamples: 4)

    camera(d: 1000, eye: p(1.6, 1, 3), lookAt: p(1.6, 0, 0), numThreads: 32)

    ambientLight(color: Color.WHITE, ls: 0.5)

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
        phong(id: "yellow", ks: 0.9, cd: c(1.0, 0.95, 0.25), ka: 0.5, kd: 0.7, exp: 10)
    }

    objects {
        //plane(point: p(0,0,0), normal: n(0, 1, 0), material: "white")

        instance(object: ply1, material: "green") {
            scale(v(3, 3, 3))
            rotateY(10)
            translate(v(1, 0, 0))
        }

        instance(object: ply2, material: "green") {
            scale(v(3, 3, 3))
            rotateY(10)
            translate(v(2, 0, 0))
        }

    }

}