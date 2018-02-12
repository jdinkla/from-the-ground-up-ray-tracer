import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

def ply1 = builder.ply(file: "/opt/rendering/ply/horse/Horse97K.ply")

boolean isTransparent = true

builder.world(id: "World45") {

    viewPlane(resolution: Resolution.RESOLUTION_1080, maxDepth: 5)

    //camera(direction: 4000, eye: p(0, 1, 5), lookAt: p(0, 0, 0))

    //camera(direction: 1500, eye: p(0, 0.1, 5), lookAt: p(0, 0, 0))
    camera(d: 1250, eye: p(0, 0.1, 2), lookAt: p(0.6, 0.5, 0), numThreads: 32)

    ambientLight(color: Color.WHITE, ls: 0.75f)

    lights {
        //pointLight(location: p(-10, 3, 0), color: c(1, 0, 0))
        //pointLight(location: p(+10, 3, 0), color: c(0, 0, 1))
        //pointLight(location: p(0, 3, 10), color: c(0, 1, 0), ls: 0.5)
        pointLight(location: p(1, 100, 1), color: c(1, 1, 1), ls: 1)
    }

    materials {
        phong(id: "grey", ks: 1.0, cd: c(0.1, 0.1, 0.1), ka: 0.5, kd: 1.0, exp: 10)
        matte(id: "sky", cd: c(0.1, 0.7, 1.0), ka: 0.75, kd: 1.0)
        phong(id: "white", ks: 0.7, cd: c(1.0, 1.0, 1.0), ka: 0.5, kd: 0.75, exp: 2)
if (isTransparent) {
        transparent(id: "red", ks: 1.0, cd: c(1.0, 0.0, 0.0), ka: 0.5, kd: 1.0, exp: 2)
        transparent(id: "green", ks: 0.75, cd: c(0.0, 0.8, 0.4), ka: 0.5, kd: 1.0, exp: 10)
        transparent(id: "blue", ks: 0.9, cd: c(0.0, 0.0, 1.0), ka: 0.5, kd: 1.0, exp: 5)
} else {
        reflective(id: "red", ks: 1.0, cd: c(1.0, 0.0, 0.0), ka: 0.5, kd: 1.0, exp: 2)
        reflective(id: "green", ks: 0.75, cd: c(0.0, 0.8, 0.4), ka: 0.5, kd: 1.0, exp: 10)
        reflective(id: "blue", ks: 0.9, cd: c(0.0, 0.0, 1.0), ka: 0.5, kd: 1.0, exp: 5)
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
        //sphere(center: p(1.125, 0, 0), radius: 0.125, material: "green")
        //sphere(center: p(1.125, 0, -2), radius: 0.125, material: "red")
        //sphere(center: p(1.125, 0, -4), radius: 0.125, material: "blue")

        instance(object: ply1, material: "blue") {
            scale(v(1, 1, 1))
            rotateY(-45)
            translate(v(-0.25, 0, 0))
        }

        instance(object: ply1, material: "red") {
            scale(v(1, 1, 1))
            rotateY(-25)
            translate(v(+0.25, 0, 0))
        }

        instance(object: ply1, material: "green") {
            scale(v(1, 1, 1))
            rotateY(-5)
            translate(v(+1, 0, 0))
        }

    }

}