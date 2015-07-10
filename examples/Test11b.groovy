final int NUM_SPHERES = 2;
final float volume = 0.1f / NUM_SPHERES;
final float radius = (float) Math.pow(0.75f * volume / Math.PI, 1.0f/3);

final Random r = new Random();

builder.world(id: "World11") {

    camera(d: 1500, eye: p(1, 2, 10), lookAt: p(0, 0, 0))

    ambientLight(ls: 0.5)

    lights {
        pointLight(location: p(2, 2, 0), ls: 3.141, color: c(0.3, 1, 0.3))
        pointLight(location: p(-2, 2, 0), ls: 3.141, color: c(0.3, 0.3, 1))
    }

    materials {
        matte(id: "m1", cd: c(0.9, 0.4, 0.4))
        matte(id: "m2", cd: c(0.8, 0.8, 0.8))

        for (int i=0; i<NUM_SPHERES; i++) {
            int exp = (int) (r.nextFloat() * 50);
            float ks = r.nextFloat()
            def col = c(r.nextFloat(), r.nextFloat(), r.nextFloat())
            phong(id: "p${i}", cd: col, ka: 0.25, kd: 0.75, exp: exp, ks: ks)
        }
    }

    objects {

        rectangle(p0: p(0, -1, 0), a: v(1, 0, 0), b: v(0, 0, 1), material: "m1")
        rectangle(p0: p(-1, -1, -1), a: v(1, 0, 0), b: v(0, 0, 1), material: "m1")
        plane(point: p(0, -1 - radius, 0), material: "m2")

        grid {
            for (int i=0; i<NUM_SPHERES; i++) {
                def cent = p(
                        1.0f - 2.0f * r.nextFloat(),
                        1.0f - 2.0f * r.nextFloat(),
                        1.0f - 2.0f * r.nextFloat())
                sphere(center: cent, radius: radius, material: "p${i}")
            }
        }
    }

}
