import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.objects.acceleration.kdtree.Simple2Builder

//String path = 'F://opt/rendering/ply'
//String path = '/opt/rendering/ply'
String path = 'examples/ply'

int NUM = 20
int NUM2 = NUM/2

def r = new Random()

builder.world(id: "World75") {

    viewPlane(resolution: Resolution.RESOLUTION_1440, maxDepth: 2, numSamples: 4)

    camera(d: 1000, eye: p(0, 0, 5), lookAt: p(0, 0, 0), numThreads: 32)

    ambientLight(color: Color.WHITE, ls: 0.5)

    lights {
        pointLight(location: p(-1, 2, 3), color: c(1, 1, 1), ls: 1)
    }

    materials {
        phong(id: "grey", ks: 1.0, cd: c(0.1, 0.1, 0.1), ka: 0.5, kd: 1.0, exp: 10)
        matte(id: "sky", cd: c(0.1, 0.7, 1.0), ka: 0.75, kd: 1.0)
        reflective(id: "white", ks: 0.7, cd: c(1.0, 1.0, 1.0), ka: 0.5, kd: 0.75, exp: 2)
        phong(id: "p0", ks: 0.7, cd: c(0.95, 0.2, 0.05), ka: 0.5, kd: 0.7, exp: 10)
        phong(id: "p1", ks: 0.7, cd: c(0.2, 0.95, 0.05), ka: 0.5, kd: 0.7, exp: 20)
        phong(id: "p2", ks: 0.7, cd: c(0.2, 0.05, 0.95), ka: 0.5, kd: 0.7, exp: 30)
        phong(id: "p3", ks: 0.9, cd: c(1.0, 0.95, 0.25), ka: 0.5, kd: 0.7, exp: 10)
    }

    objects {
        //plane(point: p(0,0,0), normal: n(0, 1, 0), material: "white")
        kdtree(builder: Simple2Builder) {
            for (int k=0; k<NUM; k++) {
                for (int j=0; j<NUM; j++) {
                    for (int i=0; i<NUM; i++) {
                        sphere(center: p(-NUM2+i, -NUM2+j, -k), radius: 0.25, material: "p${(i+j+k)%4}")
                    }
                }
            }
        }
    }

}