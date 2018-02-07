import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.math.Normal

def sph1 = builder.sphere(radius: 0.25)

builder.world(id: "World26") {

    camera(d: 250)

    ambientLight(ls: 0.5)

    lights {
        pointLight(location: p(2, 100, 1))
    }

    materials {
        matte(id: "m1", cd: c(1, 1, 0), ka: 0.75f, kd: 0.75f)
        matte(id: "m2", cd: c(1), ka: 0.75f, kd: 0.75f)
    }

    objects {
        instance(object: sph1, material: "m1") {
            scale(v(10, 17, 15))
            translate(v(0, 5, 0))
            rotateX(12)
            rotateZ(12)
        }
        plane(material: "m2", point: Point3D.ORIGIN, normal: Normal.UP)
    }

}
