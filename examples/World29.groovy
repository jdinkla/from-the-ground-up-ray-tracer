import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.math.Normal

float sqrt200 = (float) Math.sqrt(200)
def r1 = builder.alignedBox(p: Point3D.ORIGIN, q: p(sqrt200, 10, 0.52))

builder.world(id: "World29") {

    camera(d: 100)

    ambientLight(color: RGBColor.WHITE, ls: 0.5f)

    lights {
        pointLight(location: p(0, 9, 0.5), ls: 1.0f)
        pointLight(location: p(0, 9, 0.5), ls: 1.0f)
    }

    materials {

        matte(id: "red", ks: 1.0, cd: c(1, 0, 0), ka: 1.0, kd: 1.0, exp: 1)

        phong(id: "p1", ks: 0.5, cd: c(1, 1, 0), ka: 0.25, kd: 0.75, exp: 50)
        phong(id: "p2", ks: 0.5, cd: c(0, 1, 1), ka: 0.25, kd: 0.75, exp: 50)
        phong(id: "p3", ks: 0.5, cd: c(1, 0, 1), ka: 0.25, kd: 0.75, exp: 50)
        phong(id: "p4", ks: 0.5, cd: c(1, 0.75, 0.75), ka: 0.25, kd: 0.75, exp: 50)

        matte(id: "m1", ks: 1.0, cd: c(1, 1, 1), ka: 0.25, kd: 0.9, exp: 1)

    /*
        phong(id: "m1", ks: 1.0, cd: c(1, 1, 1), ka: 0.25, kd: 0.9, exp: 1)
        phong(id: "m2", ks: 0.5, cd: c(0.1, 0.7, 0.3), ka: 0.25, kd: 0.75, exp: 10)
        phong(id: "m3", ks: 0.5, cd: c(1, 1, 0), ka: 0.25, kd: 0.75, exp: 50)
        phong(id: "m4", ks: 0.1, cd: c(1, 0, 0), ka: 0.25, kd: 0.75, exp: 3)
        phong(id: "m5", ks: 0.5, cd: c(0, 0.5, 1), ka: 0.25, kd: 0.75, exp: 10)
        */
        reflective(id: "mX", ks: 1.0, cd: c(1, 1, 1), ka: 0.25, kd: 0.9, exp: 1)
        reflective(id: "m2", ks: 0.5, cd: c(0.1, 0.7, 0.3), ka: 0.25, kd: 0.75, exp: 10)
        reflective(id: "m3", ks: 0.5, cd: c(1, 1, 0), ka: 0.25, kd: 0.75, exp: 50)
        reflective(id: "m4", ks: 0.1, cd: c(1, 0, 0), ka: 0.25, kd: 0.75, exp: 3)
        reflective(id: "m5", ks: 0.5, cd: c(0, 0.5, 1), ka: 0.25, kd: 0.75, exp: 10)
    }

    objects {
         rectangle(p0: Point3D.ORIGIN, a: v(10, 0, 10), b: v(-10, 0, 10), material: "m1")

         sphere(material: "red", center: p(0, 0, 0), radius: 1)

         sphere(material: "m1", center: p(0, 4, 10), radius: 1)

         instance(material: "p1", object: r1) {
            rotateY(45)
         }

         instance(material: "p2", object: r1) {
            translate(v(-sqrt200, 0, 0))
            //rotateY(-45)
         }
/*
         instance(material: "p3", object: r1) {
            rotateY(45)
            translate(v(-10, 0, 10))
         }

         instance(material: "p4", object: r1) {
            translate(v(-sqrt200, 0, 0))
            rotateY(-45)
            translate(v(10, 0, 10))
         }
  */
    }

}
