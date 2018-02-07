import net.dinkla.raytracer.colors.Color

builder.world(id: "World38") {

    camera(d: 500, eye: p(0, 5, 10), lookAt: p(0, 1, 0), numThreads: 4)

    ambientLight(color: Color.WHITE, ls: 0.5f)

    lights {
        pointLight(location: p(0, 0, 10), ls: 1.0f)
    }

    materials {
        matte(id: "sky", cd: c(0.4, 0.7, 1.0), ka: 1.0, kd: 0.9)
        phong(id: "grey", ks: 0.4, cd: c(0.4, 0.4, 0.4), ka: 0.25, kd: 0.6, exp: 10)
        phong(id: "r", ks: 1.0, cd: Color.RED, ka: 1.0, kd: 1.0, exp: 10)
        phong(id: "g", ks: 1.0, cd: Color.GREEN, ka: 1.0, kd: 1.0, exp: 20)
        phong(id: "b", ks: 1.0, cd: Color.BLUE, ka: 1.0, kd: 1.0, exp: 20)
        phong(id: "c", ks: 1.0, cd: c(0,1,1), ka: 1.0, kd: 1.0, exp: 10)
        phong(id: "y", ks: 1.0, cd: c(1,1,0), ka: 1.0, kd: 1.0, exp: 10)
    }

    objects {
        //plane(material: "grey", point: p(0, -1, 0), normal: n(0, 1, 0))
        //plane(material: "sky", point: p(0, 100, 0), normal: n(0, -1, 0))

        grid {
            sphere(center: p(-1, -1, 0), radius: 0.25, material: "r")
        }

        grid {
            sphere(center: p(1, 1, 0), radius: 0.25, material: "r")
            sphere(center: p(2, 1, 0), radius: 0.25, material: "g")
            sphere(center: p(3, 1, 0), radius: 0.25, material: "b")
        }

        grid {
            grid {
                sphere(center: p(2, 2, 0), radius: 0.25, material: "c")
            }
            grid {
                sphere(center: p(4, 4, 0), radius: 0.25, material: "y")
            }
        }
    }

}
