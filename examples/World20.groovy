import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.math.Normal

builder.world(id: "World20") {

    camera(d: 500, eye: p(2, 2, 5), lookAt: p(1.5, 1, 0))
    
    ambientLight(color: RGBColor.WHITE, ls: 0.25f)

    lights {
        pointLight(location: new Point3D(3, 3, 1))
    }

    materials {
        matte(id: "m1", ka: 0.75f, kd: 0.75f, cd: c(1, 1, 0))
        matte(id: "m2", ka: 0.75f, kd: 0.75f, cd: c(1))
        phong(id: "m3", ka: 0.25f, kd: 0.55f, cd: c(1, 0, 0), exp: 10, ks: 0.9f, cs: RGBColor.WHITE)
    }

    objects {
        sphere(material: "m1", center: p(0, 1, 0), radius: 1)
        sphere(material: "m3", center: p(3, 1, 0), radius: 1)
        plane(material: "m2", point: Point3D.ORIGIN, normal: Normal.UP)
    }
}
