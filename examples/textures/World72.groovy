import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.textures.ImageTexture
import net.dinkla.raytracer.textures.SphericalMap

//def tex1 = new ImageTexture("/opt/rendering/textures/rtftgu/EarthHighRes.png")
//def tex1 = new ImageTexture("/opt/rendering/textures/rtftgu/uffizi_probe_large.png")
def tex1 = new ImageTexture("/opt/rendering/textures/rtftgu/SphereGrid.png")
tex1.mapping = new SphericalMap()

def sp1 = builder.sphere(center: p(0, 1, 0), radius: 1)

builder.world(id: "World70") {

    viewPlane(maxDepth: 10)

    camera(d: 750, eye: p(0, 1.2, 10), lookAt: p(0, 0.8, 0), numThreads: 30)

    ambientLight(ls: 0.5f)

    lights {
        pointLight(location: p(0, 10, 5), ls: 1.0f)
    }

    materials {
        matte(id: "sky", cd: c(0.3, 0.6, 1.0), ka: 1.0, kd: 0.9)
        phong(id: "grey", ks: 0.4, cd: c(0.4), ka: 0.25, kd: 0.6, exp: 10)
        phong(id: "r", ks: 1.0, cd: RGBColor.RED, ka: 1.0, kd: 1.0, exp: 10)
        phong(id: "g", ks: 1.0, cd: RGBColor.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        phong(id: "b", ks: 1.0, cd: RGBColor.BLUE, ka: 1.0, kd: 1.0, exp: 20)
//        svMatte(id: "tex1", kd: 0.9, ka: 0.5, cd: tex1)
        svPhong(id: "tex1", kd: 0.9, ka: 0.5, cd: tex1)
    }

    objects {
        //plane(material: "grey")
        plane(material: "sky", point: p(0, 100, 0), normal: n(0, -1, 0))

        instance(material: "tex1", object: sp1) {
//            rotateY(120)
            translate(v(-1, -1, 0))
        }

        instance(material: "tex1", object: sp1) {
//            rotateY(120)
            translate(v(-3, -1, 0))
        }

        instance(material: "tex1", object: sp1) {
            translate(v(1.5, 0, 0))
        }

        instance(material: "tex1", object: sp1) {
            translate(v(1.5, 2, 0))
        }

        instance(material: "tex1", object: sp1) {
            translate(v(1.5, -2, 0))
        }

        instance(material: "tex1", object: sp1) {
//            rotateY(120)
            translate(v(3, -1, 0))
        }

    }

}
