import net.dinkla.raytracer.colors.RGBColor
import net.dinkla.raytracer.utilities.Resolution

boolean hasShadows = true

int NUM = 10
int column = NUM/2 - 0.15
def r = new Random()

builder.world(id: "World40") {

    viewPlane(resolution: Resolution.RESOLUTION_2160, maxDepth: 10)

    camera(d: 3000, eye: p(-NUM, column+1, NUM*2), lookAt: p(column, column, NUM/2))

    ambientLight(color: RGBColor.WHITE, ls: 0.5f)

    lights {
        pointLight(location: p(NUM, NUM*2, NUM*2), ls: 1.0f, shadows: hasShadows)
    }

    materials {
        phong(id: "grey", ks: 1.0, cd: c(0.1, 0.1, 0.1), ka: 0.0, kd: 1.0, exp: 10)
        for (int i = 0; i < NUM; i++) {
            for (int j = 0; j < NUM; j++) {
                for (int k = 0; k < NUM; k++) {
                    int exp = (int) (r.nextFloat() * 50)
                    float ks = r.nextFloat()
                    def col = c(r.nextFloat(), r.nextFloat(), r.nextFloat())
                    float ka = 0.2 + r.nextFloat() * 0.8
                    float kr = r.nextFloat()
                    reflective(id: "c$i-$j-$k", ks: 0.1, cd: col, ka: 1.0, kd: 1.0, exp: exp, kr: kr)
                }
            }
        }
    }

    objects {
        grid {
            for (int i = 0; i < NUM; i++) {
                for (int j = 0; j < NUM; j++) {
                    for (int k = 0; k < NUM; k++) {
                        sphere(center: p(i, j, k), radius: 0.25, material: "c$i-$j-$k")
                    }
                }
            }
        }

    }

}
