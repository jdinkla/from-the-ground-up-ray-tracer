import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.utilities.Resolution

builder.world(id: "World10") {
    
    camera(d: 8000, eye: p(0, 0, 500), lookAt: p(0, 0, 0))

    viewPlane(resolution: new Resolution(1440))

    ambientLight(ls: 0.5)

    lights {
        pointLight(location: p(100, 50, 150), ls: 3.141)
        pointLight(location: p(-100, 50, -30), ls: 1.641, colo9r: c(0.9, 0, 0))
        pointLight(location: p(400, 180, -200), ls: 2.641, colo9r: c(0, 0.9, 1))
    }

    materials {
        phong(id: "m1", cd: c(1, 1, 0), ka: 0.25, kd: 0.65, exp: 25, ks: 1.0)
        phong(id: "m2", cd: c(0.71, 0.40, 0.16), ka: 0.25, kd: 0.65, exp: 1, ks: 0.1)
        phong(id: "m3", cd: c(0.5, 0.5, 0.5), ka: 0.25, kd: 0.55, exp: 15, ks: 0.9)
        matte(id: "m4", cd: c(0.5, 0.5, 0.99), ka: 0.1f, kd: 0.5)
    }

    objects {
        sphere(center: p(10, -5, 0), radius: 27, material: "m1")
        sphere(center: p(-30, 15, -50), radius: 27, material: "m2")
        plane(point: p(0, -100, 0), material: "m3")
        plane(point: p(0, 100, 0), normal: n(0, -1, 0), material: "m4")
    }
    
}
