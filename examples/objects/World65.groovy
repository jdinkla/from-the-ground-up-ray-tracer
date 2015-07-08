
builder.world(id: "World65") {

    camera(d: 1500, eye: p(0, 10, 1), lookAt: p(0, 0, 0))

    ambientLight(ls: 0.25)

    lights {
        pointLight(location: p(0, 10, 5), ls: 1)
    }

    materials {
        matte(id: "gray", cd: c(1), ka: 0.25, kd: 0.75)
        reflective(id: "mirror", cd: c("0000FF"), ka: 0.0, kd: 1.0, ks: 1.0, kr: 1.0, cr: c(1, 0, 1.0))
        phong(id: "Green Yellow", cd: c("adff2f"), ka: 0.5, kd: 0.75, ks: 0.55, exp: 2)
        phong(id: "Light Salmon", cd: c("ffa07a"), ka: 0.5, kd: 0.75, ks: 0.55, exp: 15)
        phong(id: "Pink", cd: c("ffc0cb"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 9)
        phong(id: "Gold1", cd: c("FFD700"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 5)
        phong(id: "Gold2", cd: c("EEC900"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 5)
    }

    objects {
        plane(material: "gray", point: p(0, -1, 0))

        torus(a: 1, b: 0.05, material: "Green Yellow")
    }

}
