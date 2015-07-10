import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.utilities.Resolution

final def r = new java.util.Random()

final int NUM = 20

float rb = 0.025

float rx = 0.5
float w = rx
float h = rx
float d = rx

float step = 0.05
float delta = 0.01

float df = 0.5

int absMaxHeight = NUM                    


builder.world(id: "World78") {

    viewPlane(resolution: Resolution.RESOLUTION_720, maxDepth: 10)
    
//    camera(d: 1000, eye: p(NUM/2, 1.5, 10), lookAt: p(NUM/2, 1.5, 0), numThreads: 32)
//    camera(d: 1000, eye: p(NUM/2, 1.5, 10), lookAt: p(NUM/2, 1.5, 0), numThreads: 64, ray: SampledRenderer, rayNumSamples: 64)
//    camera(d: 2500, eye: p(NUM/2, NUM, 20), lookAt: p(NUM/2, 1.5, -NUM/2), numThreads: 64, ray: SampledRenderer, rayNumSamples: 16)
    camera(d: 2500, eye: p(NUM/2, NUM, 20), lookAt: p(NUM/2, 1.5, -NUM/2), numThreads: 64)

    ambientLight(ls: 0.9)

    lights {
        pointLight(location: p(-5, NUM, -NUM/2), ls: 1)
        //pointLight(location: p(NUM/2, 1.5, 10), ls: 1)
//        directional(direction: v(0.1, -0.1, 1), )
        //directional(direction: v(1, 1, 1))
    }

    materials {
        matte(id: "gray", cd: c(1), ka: 0.25, kd: 0.75)
        matte(id: "sky", cd: c("a0a0ee"), kd: 1.0, ka: 1.0f)
        phong(id: "mirror", cd: c("0000FF"), ka: 0.0, kd: 1.0, ks: 1.0, kr: 1.0, cr: c(1, 0, 1.0))

        phong(id: "Green Yellow", cd: c("adff2f"), ka: 0.5, kd: 0.75, ks: 0.55, exp: 2)
        phong(id: "Light Salmon", cd: c("ffa07a"), ka: 0.5, kd: 0.75, ks: 0.55, exp: 15)
        phong(id: "Pink", cd: c("ffc0cb"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 9)
        phong(id: "Gold1", cd: c("FFD700"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 5)
        phong(id: "Gold2", cd: c("EEC900"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 5)

        for (int iz=0; iz<NUM; iz++) {
            for (int iy=0; iy<NUM; iy++) {
                for (int ix=0; ix<NUM; ix++) {
                    float fr = 0.75 * r.nextFloat() + 0.25
                    float g = 0.75 * r.nextFloat() + 0.25
                    float b = 0.75 * r.nextFloat() + 0.25
//                    reflective(id: "c-$ix-$iy-$iz", cd: c(fr, g, b), ka: 0.5, kd: 0.75, ks: 0.75, exp: 5)
                    reflective(id: "c-$ix-$iy-$iz", ka: 0.0, kd: 0.0, cd: c(fr, g, b), ks: 1, cs: c(fr, g, b), exp: 5, kr: 0.3, cr: c(fr, g, b) )
                }
            }
        }
    }

    objects {
        plane(material: "mirror")
        plane(material: "sky", point: p(0, 1000, 0), normal: Normal.DOWN)

        kdtree {
            for (int iz=0; iz<NUM; iz++) {
                for (int ix=0; ix<NUM; ix++) {
                    int maxHeight = r.next((int) Math.sqrt(iz + 1))
                    float height = 0
                    float nextHeight = height + h
                    float w2 = w
                    float d2 = d
                    float h2 = h
                    float dx = r.nextFloat() * df - df/2
                    float dz = r.nextFloat() * df - df/2
                    for (int iy=0; iy<maxHeight; iy++) {
                        float x = ix - w2/2 + dx
                        float z = iz - w2/2 + dz
                        beveledBox(p0: p(x, height, -z), p1: p(x+w2, nextHeight, -(z+d2)), rb: rb, material: "c-$ix-$iy-$iz")
                        w2 -= step
                        d2 -= step
                        h2 -= step
                        height = nextHeight + delta
                        nextHeight = height + h2
                    }
                }
            }            
        }
    }

}
