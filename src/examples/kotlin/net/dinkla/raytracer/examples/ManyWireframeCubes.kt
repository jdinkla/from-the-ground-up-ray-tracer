package net.dinkla.raytracer.examples

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition
import net.dinkla.raytracer.world.rand
import net.dinkla.raytracer.world.randInt
import net.dinkla.raytracer.world.repeat3
import kotlin.math.sqrt

object ManyWireframeCubes : WorldDefinition {
    override val id: String = "ManyWireframeCubes.kt"

    private const val NUM = 20
    private const val rb = 0.025
    private const val rx = 0.5
    private const val w = rx
    private const val h = rx
    private const val d = rx
    private const val step = 0.05
    private const val delta = 0.01
    private const val df = 0.5

    override fun world(): World =
        Builder.build {
            camera(d = 2500.0, eye = p(NUM / 2, NUM, 20), lookAt = p(NUM / 2.0, 1.5, -NUM / 2.0))

            ambientLight(ls = 0.9)

            lights {
                pointLight(location = p(-5.0, 1.0 * NUM, -NUM / 2.0), ls = 1.0)
                pointLight(location = p(5.0, 1.0 * NUM, -NUM / 2.0), ls = 1.0)
                pointLight(location = p(-5.0, 1.0 * NUM, NUM / 2.0), ls = 1.0)
                pointLight(location = p(5.0, 1.0 * NUM, NUM / 2.0), ls = 1.0)
            }

            materials {
                matte(id = "gray", cd = c(1.0), ka = 0.25, kd = 0.75)
                matte(id = "sky", cd = c("a0a0ee"), kd = 1.0, ka = 1.0)
                phong(id = "mirror", cd = c("0000FF"), ka = 0.0, kd = 1.0, ks = 1.0)
                repeat3(NUM) { ix, iy, iz ->
                    val fr = 0.75 * rand() + 0.25
                    val g = 0.75 * rand() + 0.25
                    val b = 0.75 * rand() + 0.25
                    phong(
                        id = "c-$ix-$iy-$iz",
                        ka = 0.0,
                        kd = 0.75,
                        cd = c(fr, g, b),
                        ks = 1.0,
                        cs = c(fr, g, b),
                        exp = 5.0,
                    )
                }
            }

            objects {
                plane(material = "mirror", point = p(0.0, -0.01, 0.0), normal = Normal.UP)
                plane(material = "sky", point = p(0, 1000, 0), normal = Normal.DOWN)
                grid {
                    for (iz in 0..<NUM) {
                        for (ix in 0..<NUM) {
                            val maxHeight = randInt(sqrt(iz + 1.0).toInt())
                            var height = 0.0
                            var nextHeight = height + h
                            var w2 = w
                            var d2 = d
                            var h2 = h
                            val dx = rand() * df - df / 2
                            val dz = rand() * df - df / 2
                            for (iy in 0..<maxHeight) {
                                val x = ix - w2 / 2 + dx
                                val z = iz - w2 / 2 + dz
                                beveledBox(
                                    p0 = p(x, height, -z),
                                    p1 = p(x + w2, nextHeight, -(z + d2)),
                                    rb = rb,
                                    material = "c-$ix-$iy-$iz",
                                    isWiredFrame = true,
                                )
                                w2 -= step
                                d2 -= step
                                h2 -= step
                                height = nextHeight + delta
                                nextHeight = height + h2
                            }
                        }
                    }
                }
            }
        }
}
