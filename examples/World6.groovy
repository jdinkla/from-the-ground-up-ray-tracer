import net.dinkla.raytracer.utilities.Resolution

builder.world(id: "World6") {

    viewPlane(
        resolution: Resolution.RESOLUTION_720
            //, numSamples: 4
    )

    camera(d: 750, eye: p(0, 30, 80), lookAt: p(0, 10, 0), numThreads: 4)

    ambientLight(ls: 0.25f)

    lights {
        pointLight(location: p(100, 50, 150))
    }

    materials {
//        matte(id: "m1", cd: c(1, 1, 0), ka: 0.25, kd: 0.65)
//        matte(id: "m2", cd: c(0.71, 0.40, 0.16), ka: 0.25, kd: 0.65)
//        matte(id: "m3", cd: c(0.7), ka: 0.25, kd: 0.65)
        reflective(id: "m1", cd: c(1, 1, 0), ka: 0.25, kd: 0.65)
        reflective(id: "m2", cd: c(0.71, 0.40, 0.16), ka: 0.25, kd: 0.65)
        reflective(id: "m3", cd: c(0.7), ka: 0.25, kd: 0.65)
    }

    objects {
        sphere(material: "m1", center: p(10, -5, 0), radius: 27)
        sphere(material: "m2", center: p(-30, 15, -50), radius: 27)
        plane(material: "m3")
    }
}
