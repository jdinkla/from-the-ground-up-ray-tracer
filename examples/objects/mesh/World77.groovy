import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.objects.acceleration.kdtree.KDTree
import net.dinkla.raytracer.objects.acceleration.kdtree.ObjectMedian2Builder

//String path = 'F://opt/rendering/ply'
String path = 'C://opt/rendering/ply'
//String path = '/opt/rendering/ply'

//def ply1 = builder.ply(file: "${path}/dragon/dragon_recon/dragon_vrip_res4.ply", smooth: true,
//        type: KDTree, builder: SpatialMedianBuilder, maxDepth: 15, minChildren: 5)

def ply1 = builder.ply(file: "${path}/goldfish_low_res.ply", smooth: true, binary: true,
//        type: KDTree, builder: SpatialMedianBuilder, maxDepth: 15, minChildren: 5)
        type: KDTree, builder: ObjectMedian2Builder, maxDepth: 15, minChildren: 5)

//def ply1 = builder.ply(file: "${path}/dragon/dragon_recon/dragon_vrip_res4.ply", smooth: true)

def r = new Random()
int NUM = 1
int NUM2 = NUM / 2

builder.world(id: "World77") {

    viewPlane(resolution: Resolution.RESOLUTION_1080, maxDepth: 2, numSamples: 4)

    camera(d: 500, eye: p(0.4, 1, 10), lookAt: p(0.4, 0, 0), numThreads: 32)
//    camera(direction: 500, eye: p(NUM2, 1, 5), lookAt: p(NUM2, 0, -NUM2), numThreads: 64)

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
        plane(point: p(0,-10,0), normal: n(0, 1, 0), material: "white")

        kdtree {
            for (int k=0; k<NUM; k++) {
                for (int j=0; j<NUM; j++) {
                    for (int i=0; i<NUM; i++) {
                        instance(object: ply1, material: "green") {
                            scale(v(1.5, 1.5, 1.5))
                            rotateY(10)
                            translate(v(i, 0, -k))
                        }
                    }
                }

            }            
        }

    }

}