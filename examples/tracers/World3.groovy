import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.tracers.MultipleObjects

builder.world(id: "World3") {

    tracer(type: MultipleObjects)

    camera(d: 750, eye: p(0, 0, 200), lookAt: p(50, 0, 0))

    ambientLight(ls: 0.5f)

    lights {
        pointLight(location: new Point3D(3, 3, 1))
    }

    materials {
        matte(id: "m1", cd: c(1, 0, 0))
        matte(id: "m2", cd: c(0, 1, 0))
        matte(id: "m3", cd: c(0, 0, 1))
    }

    objects {
        sphere(material: "m1", center: p(0, 50, 0), radius: 10)
        sphere(material: "m2", center: p(20, 10, 0), radius: 10)
        sphere(material: "m3", center: p(80, 0, 0), radius: 30)
    }

}
