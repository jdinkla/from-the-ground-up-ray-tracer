import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.utilities.Resolution

builder.world(id: "World43") {

    viewPlane(resolution: Resolution.RESOLUTION_480, maxDepth: 5)

    camera(d: 1000, eye: p(0, 1, 5), lookAt: p(0, 0, 0), numThreads: 4)

    ambientLight(color: Color.WHITE, ls: 0.5f)

    lights {
        pointLight(location: p(0.6, 0.3, 2))
    }

    materials {
        phong(id: "grey", ks: 1.0, cd: c(0.1, 0.1, 0.1), ka: 0.0, kd: 1.0, exp: 10)
        phong(id: "green", ks: 1.0, cd: c(0.1, 0.9, 0.4), ka: 0.5, kd: 1.0, exp: 10)
    }

    objects {
        //tesselatedSmoothSphere(m: 3, n: 2, material: "green")
        //tesselatedSmoothSphere(m: 4, n: 2, material: "green")
        //tesselatedSmoothSphere(m: 10, n: 5, material: "green")

        grid {
//            tesselatedSmoothSphere(m: 20, n: 10, material: "green")
            tesselatedSmoothSphere(m: 100, n: 50, material: "green")
        }
        //tesselatedSmoothSphere(m: 100, n: 50, material: "green")


        //tesselatedSmoothSphere(m: 4, n: 2, material: "green")
    }

}
