package objects.beveled

import net.dinkla.raytracer.math.Normal

def r = new java.util.Random()

final int NUM_COLORS = 4
final int NUM_OBJECTS = 1000

builder.world(id: "World69b") {

    camera(d: 850, eye: p(-1, 3.5, 5), lookAt: p(2.9, 0, 0))

    ambientLight(ls: 0.5)

    lights {
        pointLight(location: p(0, 10, 5), ls: 1)
    }

    materials {
        matte(id: "gray", cd: c(1), ka: 0.25, kd: 0.75)
        matte(id: "green", cd: c("009933"), ka: 0.5, kd: 1.0)
        matte(id: "skyblue", cd: c("5CD6FF"), ka: 1.0, kd: 1.0)

        for (int i=0; i<NUM_COLORS; i++) {
            float f = 0.75
            float fr = (1.0-f) * r.nextFloat() + f
            float fg = (1.0-f) * r.nextFloat() + f
            float fb = (1.0-f) * r.nextFloat() + f
            float fka = r.nextFloat()
            float fkd = r.nextFloat()
            float fks = r.nextFloat()
            phong(id: "c-$i", cd: c(fr, fg, fb), ka: fka, kd: fkd, ks: fks, exp: 5, ior: 1.02)
        }
    }

    objects {
        plane(material: "green")
        plane(material: "skyblue", point: p(0, 100, 0), normal: Normal.DOWN)

        grid {
            for (int i=0; i<NUM_OBJECTS; i++) {
                def x = r.nextFloat() * (i % 50)
                def y = 0
                def z = r.nextFloat() * -1 * (i % 50)
                def x2 = x + r.nextFloat()
                def y2 = y + r.nextFloat()
                def z2 = z + r.nextFloat()
                def c = r.nextInt(NUM_COLORS)
                def mat = "c-$c"
                alignedBox(p: p(x, y, z), q: p(x2, y2, z2), material: mat)
            }
        }
    }

}
