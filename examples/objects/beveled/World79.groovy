import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.cameras.render.SampledRenderer

final def r = new java.util.Random()

final int NUM = 10

float rb = 0.025

float rx = 0.5
float w = rx
float h = rx
float d = rx

float step = 0.05
float delta = 0.01


builder.world(id: "World79") {

    viewPlane(resolution: Resolution.RESOLUTION_720, maxDepth: 10)
//    viewPlane(resolution: Resolution.RESOLUTION_320, maxDepth: 10)

    camera(d: 1000, numThreads: 32, eye: p(0, 3, 3), ray: SampledRenderer, rayNumSamples: 16)
//    camera(d: 1000, numThreads: 32, eye: p(0, 3, 3))
//    camera(d: 1000, numThreads: 32, eye: p(3, 3, 0))
//    camera(d: 1000, numThreads: 32, eye: p(0, 3, -3))
//    camera(d: 1000, numThreads: 32, eye: p(-3, 3, 0))

    ambientLight(ls: 0.25)

    lights {
        pointLight(location: p(2, 5, 1))
    }
        
    materials {
        matte(id: "gray", cd: c(1), ka: 0.25, kd: 0.75)

        phong(id: "Green Yellow", cd: c("adff2f"), ka: 0.5, kd: 0.75, ks: 0.55, exp: 2)
        phong(id: "Light Salmon", cd: c("ffa07a"), ka: 0.5, kd: 0.75, ks: 0.55, exp: 15)
        phong(id: "Pink", cd: c("ffc0cb"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 9)
        phong(id: "Gold1", cd: c("FFD700"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 5)
        phong(id: "Gold2", cd: c("EEC900"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 5)
    }

    objects {
        plane(material: "gray")

        //kdtree {
            beveledBox(p0: p(-0.5, -0.5, -0.5), p1: p(0.5, 0.5, 0.5), material: "Light Salmon", rb: 0.05)            
        //}
    }

}
