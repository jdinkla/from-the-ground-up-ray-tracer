package net.dinkla.raytracer.examples

import net.dinkla.raytracer.cameras.render.Renderers
import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition
import java.util.*

object World42 : WorldDefinition {

    const val hasShadows = true
    const val NUM = 25
    private const val column = NUM / 2 - 0.15
    val r = Random()

    override fun world() = Builder.build("World42") {
        //        viewPlane(resolution= Resolution.RESOLUTION_1080, maxDepth= 10)

//    camera(direction= 1000, eye= p(-NUM, column+1, NUM*2), lookAt= p(column, column, NUM/2), type= IterativePinhole)
//    camera(direction= 1000, eye= p(-5, NUM + 5, NUM + 5), lookAt= p(column, NUM - 5, column), type= IterativePinhole)
//    camera(direction= 1000, eye= p(-5, NUM + 5, NUM + 5), lookAt= p(column, NUM - 10, column), type= IterativePinhole)
//    camera(direction= 1000, eye= p(-5, NUM + 5, NUM + 5), lookAt= p(column, NUM - NUM/5, column), type= PinholePar)
//        camera(d= 1000.0, eye= p(-1, NUM, NUM + 5), lookAt= p(column, NUM - NUM/5, column), numThreads= 30)
        camera(d = 1000.0, eye = p(-1, NUM, NUM + 5), lookAt = p(column, NUM - NUM / 5.0, column), engine = Renderers.COROUTINE)

        ambientLight(color = Color.WHITE, ls = 0.5)

        lights {
//            pointLight(location = p(NUM, NUM * 2, NUM * 2), ls = 1.0, shadows = hasShadows)
            pointLight(location = p(NUM, NUM * 2, NUM * 2), ls = 1.0)
        }

        materials {
            reflective(id = "grey", ks = 1.0, cd = c(0.1, 0.1, 0.1), ka = 0.0, kd = 1.0, exp = 10.0)
            for (i in 0 until NUM) {
                for (j in 0 until NUM) {
                    for (k in 0 until NUM) {
                        val exp = r.nextDouble() * 50
                        val col = c(r.nextDouble(), r.nextDouble(), r.nextDouble())
                        val ka = 0.5 // 0.2 + r.nextFloat() * 0.8
                        val kd = 0.5
                        val ks = 1.0 // r.nextFloat()
                        val kr = 1.0 // r.nextFloat()
                        //phong(id= "c$i-$j-$k", ks= 1.0, cd= col, ka= 1.0, kd= 1.0, exp= exp)
//                    reflective(id= "c$i-$j-$k", ks= 1.0, cd= col, ka= 1.0, kd= 1.0, exp= exp, kr= kr)
//                    reflective(id= "c$i-$j-$k", ks= ks, cd= col, ka= ka, kd= kd, exp= exp, kr= kr)
                        reflective(id = "c$i-$j-$k", ka = ka, kd = kd, cd = col, ks = ks, cs = col, kr = kr, cr = col, exp = exp)
                    }
                }
            }
        }

        objects {
            plane(material = "grey", point = p(0, 0, -5), normal = n(0, 0, 1))    // hinten
            plane(material = "grey", point = p(0, -5, 0), normal = n(0, 1, 0))    // unten
            plane(material = "grey", point = p(NUM + 5, 0, 0), normal = n(-1, 0, 0))    // rechts hinten
            plane(material = "grey", point = p(0, NUM * 2 + 5, 0), normal = n(0, -1, 0)) // oben
            plane(material = "grey", point = p(0, 0, NUM * 2 + 5), normal = n(0, 0, -1)) // rechts vorne
            plane(material = "grey", point = p(-NUM - 5, 0, 0), normal = n(1, 0, 0)) // links

            //        sphere(center= p(0, 0, 0), radius= 0.25, material= "r")
            grid {
                for (i in 0 until NUM) {
                    for (j in 0 until NUM) {
                        for (k in 0 until NUM) {
                            sphere(center = p(i, j, k), radius = 0.25, material = "c$i-$j-$k")
                        }
                    }
                }
            }

        }
    }
}
