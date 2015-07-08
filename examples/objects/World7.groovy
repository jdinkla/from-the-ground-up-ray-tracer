import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.math.Normal

builder.world(id: "World7") {

    camera(d: 750, eye: p(0, 100, 200), lookAt: p(0, 0, 0))
    
    ambientLight(color: RGBColor.WHITE, ls: 1.0f)

    lights {
        pointLight(location: p(100, 50, 150), ls: 3.14f)
        pointLight(location: p(-100, 50, -30), ls: 1.6f, color: c(0.9f, 0.0f, 0.0f))
        pointLight(location: p(200, 180, 10), ls: 2.6f, color: c(0.0f, 0.9f, 1.0f))
    }

    materials {
        phong(id: "m1", cd: c(1, 1, 0), ka: 0.25f, kd: 0.65f)
        phong(id: "m2", cd: c(0.71f, 0.40f, 0.16f), ka: 0.25f, kd: 0.65f)
        matte(id: "m3", cd: c(0.9f, 0.9f, 0.9f), ka: 0.25f, kd: 0.65f)
        matte(id: "m4", cd: c(0.2f, 0.4f, 0.7f), ka: 0.25f, kd: 0.65f)
        matte(id: "m5", cd: c(0.4f, 0.7f, 0.2f), ka: 0.25f, kd: 0.65f)
        matte(id: "m6", cd: c(0.8f, 0.0f, 0.61f), ka: 0.25f, kd: 0.65f)
    }

    objects {
        sphere(material: "m1", center: p(10, -5, 0), radius: 27)
        sphere(material: "m2", center: p(-30, 15, -50), radius: 27)
        plane(material: "m3", point: Point3D.ORIGIN, normal: Normal.UP)
        triangle(material: "m4", a: p(-30, 0, 0), b: p(0, 30, 0), c: p(-30, 20, 10))
        disk(material: "m5", center: p(-50, 10, 0), radius: 15, normal: n(1, 1, -1))
        rectangle(material: "m6", p0: p(20, 20, 60), a: v(5, 0, 0), b: v(0, 10, 0))
    }

}
