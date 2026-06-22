package net.dinkla.raytracer.examples.cameras

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.AlignedBox
import net.dinkla.raytracer.world.Builder.build
import net.dinkla.raytracer.world.WorldDefinition

/**
 * The sharp single-ray control for [DepthOfFieldDemo] (TASK-30): identical geometry, lighting and
 * thin-lens camera, but no `samples(...)` call. The single-ray path uses the lens centre and so
 * ignores the aperture entirely — every box renders sharp. Rendering this beside [DepthOfFieldDemo]
 * isolates the depth-of-field blur as the one difference the sampled render path adds.
 */
object DepthOfFieldDemoSharp : WorldDefinition {
    override val id: String = "DepthOfFieldDemoSharp.kt"

    override fun world() =
        build {
            val box = AlignedBox(p = p(0, 0, 0), q = p(1, 2, 1))

            thinLensCamera(
                d = 1000.0,
                f = 74.0,
                lensRadius = 0.6,
                eye = p(2, 1, 10),
                lookAt = p(2, 1, 0),
            )

            ambientLight(ls = 0.6)

            lights {
                pointLight(location = p(4.5, 3.0, 7.0), ls = 2.5)
                pointLight(location = p(-2.0, 4.0, 8.0), ls = 1.5)
            }

            materials {
                phong(id = "m1", ks = 1.0, cd = c(1, 1, 1), ka = 0.4, kd = 0.9, exp = 1.0)
                phong(id = "m2", ks = 0.5, cd = c(0.1, 0.7, 0.3), ka = 0.25, kd = 0.75, exp = 10.0)
                phong(id = "m3", ks = 0.5, cd = c(1, 1, 0), ka = 0.25, kd = 0.75, exp = 50.0)
                phong(id = "m4", ks = 0.1, cd = c(1, 0, 0), ka = 0.25, kd = 0.75, exp = 3.0)
            }

            objects {
                plane(material = "m1", point = Point3D.ORIGIN, normal = Normal.UP)
                plane(material = "m1", point = p(0, 0, -700), normal = Normal.BACKWARD)
                instance(of = box, material = "m4") {
                    translate(v(0, 0, 0))
                }
                instance(of = box, material = "m2") {
                    translate(v(2, 0, -20))
                }
                instance(of = box, material = "m3") {
                    translate(v(6, 0, -50))
                }
            }
        }
}
