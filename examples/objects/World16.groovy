import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.math.Normal

builder.world(id: "World16") {

    camera(d: 1000)
    
    ambientLight(ls: 0.5)

    lights {
        pointLight(location: p(2, 2, 1), ls: 1)
    }

    materials {
        matte(id: "m1", cd: c(1, 1, 0), ka: 0.75f, kd: 0.75f)
        matte(id: "m2", cd: c(1), ka: 0.75f, kd: 0.75f)
    }

    objects {
        openCylinder(material: "m1", y0: 0.2f, y1: 1.5f, radius: 0.9f)
        plane(material: "m2", point: Point3D.ORIGIN, normal: Normal.UP)
    }

}
