import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.math.Normal

def p(x, y, z) { new Point3D(x, y, z) }
def c(r, g, b) { new Color(r, g, b) }
def v(x, y, z) { new Vector3D(x, y, z) }
def n(x, y, z) { new Normal(x, y, z) }

builder.world(id: "World33") {

    camera(d: 500, eye: p(3, 1, 5), lookAt: p(3, 0, 0))
    
    ambientLight(ls: 0.5f)

    lights {
        pointLight(location: p(0, 0, 5), ls: 1.0f)
    }

    materials {
        matte(id: "sky", cd: c(0.3, 0.6, 1.0), ka: 1.0, kd: 0.9)
        phong(id: "grey", ks: 0.4, cd: c(0.7), ka: 0.25, kd: 0.6, exp: 10)
        phong(id: "r", ks: 1.0, cd: Color.RED, ka: 1.0, kd: 1.0, exp: 10)
        phong(id: "g", ks: 1.0, cd: Color.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        phong(id: "b", ks: 1.0, cd: Color.BLUE, ka: 1.0, kd: 1.0, exp: 20)
        reflective(id: "rr", ks: 1.0, cd: Color.RED, ka: 1.0, kd: 1.0, exp: 10)
        reflective(id: "gr", ks: 1.0, cd: Color.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        reflective(id: "br", ks: 1.0, cd: Color.BLUE, ka: 1.0, kd: 1.0, exp: 20)
    }

    objects {
        plane(material: "grey", point: p(0, -1, 0), normal: n(0, 1, 0))
        plane(material: "sky", point: p(0, 99999, 0), normal: n(0, -1, 0))

        sphere(material: "rr", center: p(1, 0, 1), radius: 0.9);
        sphere(material: "gr", center: p(3, 0, 1), radius: 0.9);
        sphere(material: "br", center: p(5, 0, 1), radius: 0.9);
    }

}
