import net.dinkla.raytracer.math.Point3DF
import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.math.Normal

def b1 = builder.alignedBox(p: p(0, 0, 0), q: p(5, 1.75f, 0.15f))
def s1 = builder.solidCylinder(y0: 0, y1: 2.5, radius: 0.5)

builder.world(id: "World26") {

    camera(d: 1000)
    
    ambientLight(color: RGBColor.WHITE, ls: 0.5f)

    lights {
        pointLight(location: p(10, 7, 0), ls: 1.0f)
    }

    materials {
        matte(id: "m1", ks: 1.0, cd: c(1, 1, 1), ka: 0.25, kd: 0.9, exp: 1)
        matte(id: "m2", ks: 0.5, cd: c(0.1, 0.7, 0.3), ka: 0.25, kd: 0.75, exp: 10)
        matte(id: "m3", ks: 0.5, cd: c(1, 1, 0), ka: 0.25, kd: 0.75, exp: 50)
        matte(id: "m4", ks: 0.1, cd: c(1, 0, 0), ka: 0.25, kd: 0.75, exp: 3)
        matte(id: "m5", ks: 0.5, cd: c(0, 0.5, 1), ka: 0.25, kd: 0.75, exp: 10)
    }

    objects {
         plane(material: "m1", point: Point3DF.ORIGIN, normal: Normal.UP)
         instance(material: "m2", object: b1) {
            rotateY(-20)
            translate(v(-3f, 0, 0))
         }
         sphere(material: "m3", center: p(0.5, 0.6, -1), radius: 0.6)
         sphere(material: "m4", center: p(-1.5, 0.4, 0.5), radius: 0.4)
         instance(material: "m5", object: s1) {
            translate(v(-0.7, 0, -1))
         }
    }

}
