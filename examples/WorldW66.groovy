
builder.world(id: "World66", dynamic: true, start: 0, end: 100, step: 1) {

    camera(d: 1000, eye: p(2.5, 1.35, 10), lookAt: p(2.5, 1, 0))

    ambientLight(ls: 0.0)

    lights {
        directionalLight(direction: v(-1, -1, -1), ls: 0.5, color: c("FFD700"))
        directionalLight(direction: v(-1.1, -1.5, -1.1), ls: 0.5, color: c("EEC900"))
        directionalLight(direction: v(-0.9, -0.2, -0.9), ls: 1.0, color: c("ffc0cb"))
    }

    materials {
        matte(id: "gray", cd: c(1), ka: 0.25, kd: 0.75)
        reflective(id: "mirror", cd: c("0000FF"), ka: 0.0, kd: 1.0, ks: 1.0, kr: 1.0, cr: c(1, 0, 1.0))
        matte(id: "Green Yellow", cd: c("adff2f"), ka: 0.5, kd: 0.75, ks: 0.05, exp: 20)
        phong(id: "Light Salmon", cd: c("ffa07a"), ka: 0.5, kd: 0.75, ks: 0.55, exp: 15)
        phong(id: "Pink", cd: c("ffc0cb"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 9)
        phong(id: "Gold1", cd: c("FFD700"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 5)
        phong(id: "Gold2", cd: c("EEC900"), ka: 0.5, kd: 0.75, ks: 0.75, exp: 5)
    }

    objects {
        plane(material: "gray")
        sphere(center: p(0,1,0), radius: 1, material: "Green Yellow")
        sphere(center: p(2,1,-20), radius: 1, material: "Green Yellow")
        sphere(center: p(4,1,-40), radius: 1, material: "Green Yellow")
        sphere(center: p(6,1,-60), radius: 1, material: "Green Yellow")
        sphere(center: p(8,1,-80), radius: 1, material: "Green Yellow")
    }

}
