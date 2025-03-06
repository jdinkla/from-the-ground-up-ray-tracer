package net.dinkla.raytracer.examples.objects.beveled

import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object BeveledBoxesWireFrame : WorldDefinition {
    override val id: String = "BeveledBoxesWireFrame.kt"

    override fun world(): World =
        Builder.build {
            camera(d = 3000.0, eye = p(-1.0, 1.2, 7.0), lookAt = p(0.75, 0.0, 0.0))

            ambientLight(ls = 0.25)

            lights {
                pointLight(location = p(0, 10, 5), ls = 1.0)
            }

            materials {
                matte(id = "gray", cd = c(1.0), ka = 0.25, kd = 0.75)
                reflective(id = "mirror", cd = c("0000FF"), ka = 0.0, kd = 1.0, ks = 1.0, kr = 1.0, cr = c(1.0, 0.0, 1.0))
                transparent(id = "Green Yellow", cd = c("adff2f"), ka = 0.5, kd = 0.75, ks = 0.55, exp = 2.0, ior = 1.02)
                transparent(id = "Light Salmon", cd = c("ffa07a"), ka = 0.5, kd = 0.75, ks = 0.55, exp = 15.0, ior = 1.02)
                transparent(id = "Pink", cd = c("ffc0cb"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 9.0, ior = 1.02)
                transparent(id = "Gold1", cd = c("FFD700"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 5.0, ior = 1.02)
                transparent(id = "Gold2", cd = c("EEC900"), ka = 0.5, kd = 0.75, ks = 0.75, exp = 5.0, ior = 0.12)
            }

            objects {
                plane(material = "mirror")
                beveledBox(
                    p0 = p(0.25, 0.0, 0.25),
                    p1 = p(0.75, 0.75, 0.75),
                    rb = 0.01,
                    material = "Green Yellow",
                    isWiredFrame = true,
                )
                beveledBox(
                    p0 = p(1.25, 0.0, 1.25),
                    p1 = p(1.5, 0.5, 2.0),
                    rb = 0.01,
                    material = "Light Salmon",
                    isWiredFrame = true,
                )
                beveledBox(
                    p0 = p(-0.9, 0.1, 1.5),
                    p1 = p(-0.6, 0.25, 2.2),
                    rb = 0.01,
                    material = "Pink",
                    isWiredFrame = true,
                )
                beveledBox(
                    p0 = p(0.85, 0.0, 1.5),
                    p1 = p(1.05, 0.25, 2.2),
                    rb = 0.01,
                    material = "Pink",
                    isWiredFrame = true,
                )
                beveledBox(
                    p0 = p(-0.1, 0.0, 1.0),
                    p1 = p(0.3, 0.45, 2.2),
                    rb = 0.01,
                    material = "Gold1",
                    isWiredFrame = true,
                )
            }
        }
}
