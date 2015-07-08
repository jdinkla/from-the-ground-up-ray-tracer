
builder.world(id: "World64") {

    camera(d: 2000, eye: p(0, 1.2, 7), lookAt: p(0.75, 0, 0))

    ambientLight(ls: 0.25)

    lights {
        pointLight(location: p(0, 10, 5), ls: 1)
    }

    materials {
        matte(id: "gray", cd: c(1), ka: 0.25, kd: 0.75)
        reflective(id: "mirror", cd: c("0000FF"), ka: 0.0, kd: 1.0, ks: 1.0, kr: 1.0, cr: c(1, 0, 1.0))
        phong(id: "Green Yellow", cd: c("adff2f"), ka: 0.5, kd: 0.75, ks: 0.55, exp: 2, ior: 1.02)
        phong(id: "Light Salmon", cd: c("ffa07a"), ka: 0.5, kd: 0.75, ks: 0.55, exp: 15, ior: 1.02)
        phong(id: "Pink", cd: c("ffc0cb"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 9, ior: 1.02)
        phong(id: "Gold1", cd: c("FFD700"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 5, ior: 1.02)
        phong(id: "Gold2", cd: c("EEC900"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 5, ior: 1.02)
    }

    objects {
        plane(material: "mirror")
        beveledBox(p0: p(0.25, 0, 0.25), p1: p(0.75, 0.75, 0.75), rb: 0.1, material: "Green Yellow", wireFrame: true)
        beveledBox(p0: p(1.25, 0, 1.25), p1: p(1.5, 0.5, 2), rb: 0.05, material: "Light Salmon", wireFrame: true)
//TODO: Bug        beveledBox(p0: p(0, 0, 1.5), p1: p(0.2, 0.25, 2.2), rb: 0.1, material: "Pink", wireFrame: true)
        beveledBox(p0: p(0.85, 0, 1.5), p1: p(1.05, 0.25, 2.2), rb: 0.02, material: "Pink", wireFrame: true)
        beveledBox(p0: p(-0.1, 0, 1), p1: p(0.3, 0.45, 2.2), rb: 0.08, material: "Gold1", wireFrame: true)
    }

}
