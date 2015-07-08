import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.utilities.Resolution
import net.dinkla.raytracer.objects.acceleration.Grid
import net.dinkla.raytracer.objects.Instance

Grid ply1 = builder.ply(file: "/opt/rendering/ply/horse/Horse97K.ply", multiplier: 2.0)

def r = new Random()

Instance inst1 = builder.instance(object: ply1) {
    scale(v(1, 1, 1))
}

final int NUM_ITEMS = 10

builder.world(id: "World47") {

    viewPlane(resolution: Resolution.RESOLUTION_480, maxDepth: 2)

    //camera(d: 1000, eye: p(0, 1, NUM_ITEMS*1.5), lookAt: p(NUM_ITEMS/2, 0, 0))

    //camera(d: 1000, eye: p(-1, 0.2, -1), lookAt: p(NUM_ITEMS/2, 0, NUM_ITEMS/2)) Bild 1
    //camera(d: 1000, eye: p(-1, 0.2, -1), lookAt: p(NUM_ITEMS/2, 0.4, NUM_ITEMS/2)) Bild 2
    //camera(d: 1000, eye: p(NUM_ITEMS/2-2, 0.0, NUM_ITEMS/2-3), lookAt: p(NUM_ITEMS/2, 0.4, NUM_ITEMS/2)) Bild 3
    camera(d: 1000, eye: p(-NUM_ITEMS/2+0.5, 0.0, 1.4), lookAt: p(-NUM_ITEMS/2, 0.4, -NUM_ITEMS/2), numThreads: 8)

    ambientLight(color: RGBColor.WHITE, ls: 0.5f)

    lights {
        pointLight(location: p(1, 100, 1), color: c(1, 1, 1), ls: 1)
    }

    materials {
        phong(id: "grey", ks: 1.0, cd: c(0.1, 0.1, 0.1), ka: 0.5, kd: 1.0, exp: 10)
        matte(id: "sky", cd: c(0.1, 0.7, 1.0), ka: 0.75, kd: 1.0)
        matte(id: "white", ks: 0.7, cd: c(1.0, 1.0, 1.0), ka: 0.5, kd: 0.75, exp: 2)
        for (int j=0; j<NUM_ITEMS; j++) {
            for (int i=0; i<NUM_ITEMS; i++) {
                int exp = (int) (r.nextFloat() * 50)
                def col = c(r.nextFloat(), r.nextFloat(), r.nextFloat())
                float ks = r.nextFloat()
//                float kd = 1.0f - ks
                float kd = 1.0f
                //phong(id: "m-$i-$j", ks: 1.0, cd: col, ka: 0.5, kd: 1.0, exp: exp, kr: 0.1, kt: 0.9, ior: 1.03 )
                phong(id: "m-$i-$j", ks: ks, cd: col, ka: 0.5, kd: kd, exp: exp)
            }
        }
    }

    objects {
        plane(point: p(0,-1,0), normal: n(0, 1, 0), material: "white")
        plane(point: p(0,100,0), normal: n(0, -1, 0), material: "sky")

//        ply(file: "examples/ply/TwoTriangles.ply", material: "green")
//        ply(file: "examples/ply/Horse2K.ply", material: "white")
        //ply(file: "examples/ply/Bunny4K.ply", material: "green")
        //ply(file: "examples/ply/Bunny16K.ply", material: "green")
        //ply(file: "examples/ply/Bunny10K.ply", material: "green")
        //ply(file: "examples/ply/teapot.ply", material: "green")

//        ply(file: "/Users/jorndinkla/Downloads/rendering/ply-examples/dragon.ply", material: "white")
//        ply(file: "/Users/jorndinkla/Downloads/rendering/ply-examples/PLYFiles/Horse97K.ply", material: "white")
        //sphere(center: p(0.7, -0.875, 1), radius: 0.125, material: "red")
        //sphere(center: p(1.125, 0, -2), radius: 0.125, material: "red")
        //sphere(center: p(1.125, 0, -4), radius: 0.125, material: "blue")

        grid {

            for (int j=0; j<NUM_ITEMS; j++) {
                for (int i=0; i<NUM_ITEMS; i++) {
                    instance(object: inst1, material: "m-$i-$j") {
                        rotateY((int) (r.nextFloat()*360))
                        translate(v(-i + r.nextFloat()/2, 0, -j + r.nextFloat()/2))                    
                    }
                }
            }
        }
    }

}