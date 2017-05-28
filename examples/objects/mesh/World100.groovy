package objects.mesh

import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.objects.acceleration.Grid

//String path = '/Users/jorndinkla/Downloads/rendering/ply-examples/happy_recon/'
//String path = 'F://opt/rendering/ply'
String path = '/opt/rendering/ply'
//Grid ply1 = builder.ply(file: "${path}/happy/happy_recon/happy_vrip_res2.ply", multiplier: 2.0, smooth: true)
//Grid ply1 = builder.ply(file: "${path}/dragon/dragon_recon/dragon_vrip_res4.ply", multiplier: 2.0, smooth: true)
//Grid ply1 = builder.ply(file: "${path}/dragon/dragon_recon/dragon_vrip_res2.ply", multiplier: 2.0, smooth: true, sparse: true)

Grid ply1 = builder.ply(file: "${path}/dragon/dragon_recon/dragon_vrip_res4.ply", multiplier: 2.0, smooth: true)
Grid ply2 = builder.ply(file: "${path}/bunny/bunny4K.ply", multiplier: 2.0, smooth: true)
Grid ply3 = builder.ply(file: "${path}/happy/happy_recon/happy_vrip_res4.ply", multiplier: 2.0, smooth: true)

def r = new Random()

builder.world(id: "World73") {

//    viewPlane(resolution: Resolution.RESOLUTION_480, maxDepth: 2, numSamples: 4)
    viewPlane(resolution: Resolution.RESOLUTION_1080, maxDepth: 16, numSamples: 64)

    camera(d: 10000, eye: p(0, 1, 3), lookAt: p(1.3, 0, 0), numThreads: 64)

    ambientLight(color: RGBColor.WHITE, ls: 0.5f)

    lights {
        pointLight(location: p(-1, 2, 3), color: c(1, 1, 1), ls: 1)
    }

    materials {
        phong(id: "grey", ks: 1.0, cd: c(0.1, 0.1, 0.1), ka: 0.5, kd: 1.0, exp: 10)
        matte(id: "sky", cd: c(0.1, 0.7, 1.0), ka: 0.75, kd: 1.0)

        reflective(id: "white", ks: 0.2, cd: c(1.0, 1.0, 1.0), ka: 0.5, kd: 0.75, kr: 0.5, exp: 2)
        reflective(id: "red", ks: 0.2, cd: c(0.95, 0.2, 0.05), ka: 0.5, kd: 0.7, kr: 0.5, exp: 10)
        reflective(id: "green", ks: 0.2, cd: c(0.2, 0.95, 0.05), ka: 0.5, kd: 0.7, kr: 0.5, exp: 20)
        reflective(id: "blue", ks: 0.2, cd: c(0.2, 0.05, 0.95), ka: 0.5, kd: 0.7, kr: 0.5, exp: 30)
        reflective(id: "yellow", ks: 0.2, cd: c(1.0, 0.95, 0.25), ka: 0.5, kd: 0.7, kr: 0.5, exp: 10)

        reflective(id: "orange", ks: 0.2, cd: c(0.95, 0.75, 0.05), ka: 0.5, kd: 0.7, kr: 0.5, exp: 10)

//        phong(id: "white", ks: 0.2, cd: c(1.0, 1.0, 1.0), ka: 0.5, kd: 0.75, kr: 0.5, exp: 2)
//        phong(id: "red", ks: 0.2, cd: c(0.95, 0.2, 0.05), ka: 0.5, kd: 0.7, kr: 0.5, exp: 10)
//        phong(id: "green", ks: 0.2, cd: c(0.2, 0.95, 0.05), ka: 0.5, kd: 0.7, kr: 0.5, exp: 20)
//        phong(id: "blue", ks: 0.2, cd: c(0.2, 0.05, 0.95), ka: 0.5, kd: 0.7, kr: 0.5, exp: 30)
//        phong(id: "yellow", ks: 0.2, cd: c(1.0, 0.95, 0.25), ka: 0.5, kd: 0.7, kr: 0.5, exp: 10)

//        phong(id: "red", ks: 0.7, cd: c(0.95, 0.2, 0.05), ka: 0.5, kd: 0.7, exp: 10)
//        phong(id: "green", ks: 0.7, cd: c(0.2, 0.95, 0.05), ka: 0.5, kd: 0.7, exp: 20)
//        phong(id: "blue", ks: 0.7, cd: c(0.2, 0.05, 0.95), ka: 0.5, kd: 0.7, exp: 30)
//        phong(id: "yellow", ks: 0.9, cd: c(1.0, 0.95, 0.25), ka: 0.5, kd: 0.7, exp: 10)
    }

    objects {

        plane(point: p(0,0,0), normal: n(0, 1, 0), material: "grey")

        plane(point: p(0,10,0), normal: n(0, -1, 0), material: "sky")

        instance(object: ply1, material: "red") {
            scale(v(3, 3, 3))
            translate(v(0, 0, 0))
        }

        instance(object: ply2, material: "green") {
            scale(v(3, 3, 3))
            rotateY(10)
            translate(v(1, 0, 0))
        }

        instance(object: ply3, material: "yellow") {
            scale(v(3, 3, 3))
            translate(v(1, 0, 1))
        }

    }

}