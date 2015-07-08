import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.utilities.Resolution

builder.world(id: "World43") {

    viewPlane(resolution: Resolution.RESOLUTION_480, maxDepth: 5)

    camera(d: 1000, eye: p(0, 1, 5), lookAt: p(0, 0, 0), numThreads: 4)

    ambientLight(color: RGBColor.WHITE, ls: 0.5f)

    lights {
        pointLight(location: p(2, 2, 2))
    }

    materials {
        phong(id: "grey", ks: 1.0, cd: c(0.1, 0.1, 0.1), ka: 0.0, kd: 1.0, exp: 10)
        phong(id: "green", ks: 1.0, cd: c(0.0, 0.8, 0.4), ka: 0.5, kd: 1.0, exp: 10)
    }

    objects {
//        tesselatedFlatSphere(m: 3, n: 2, material: "green")
        //tesselatedFlatSphere(m: 4, n: 2, material: "green")
//        tesselatedFlatSphere(m: 10, n: 5, material: "green")
        //tesselatedFlatSphere(m: 20, n: 10, material: "green")
//        tesselatedFlatSphere(m: 100, n: 50, material: "green")

        grid {
            //tesselatedFlatSphere(m: 4, n: 2, material: "green")
            tesselatedFlatSphere(m: 100, n: 50, material: "green")
            //tesselatedFlatSphere(m: 250, n: 150, material: "green")
        }
    }

}
