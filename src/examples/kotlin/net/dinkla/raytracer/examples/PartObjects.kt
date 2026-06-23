package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.OpenCone
import net.dinkla.raytracer.objects.PartCylinder
import net.dinkla.raytracer.objects.PartTorus
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition
import kotlin.math.PI

/**
 * Coverage example (TASK-40) for the "partial sweep" primitive family: [net.dinkla.raytracer.objects.Annulus],
 * [net.dinkla.raytracer.objects.PartAnnulus], [net.dinkla.raytracer.objects.PartSphere],
 * [PartCylinder], [PartTorus] and [OpenCone]. The latter three are defined on the origin's y-axis, so
 * they are placed across the row with `instance { translate(...) }`; the ring/sphere objects carry a
 * `center` and are positioned directly.
 */
object PartObjects : WorldDefinition {
    override val id: String = "PartObjects.kt"

    override fun world() =
        Builder.build {
            camera(d = 1600.0, eye = p(0.0, 5.0, 14.0), lookAt = p(0.0, 0.8, 0.0))

            ambientLight(color = Color.WHITE, ls = 0.4)

            lights {
                pointLight(location = p(-6, 8, 12), ls = 3.0)
                pointLight(location = p(8, 6, 10), ls = 2.0, color = c(1.0, 0.9, 0.8))
            }

            materials {
                matte(id = "ground", cd = c(0.7, 0.7, 0.7), ka = 0.3, kd = 0.7)
                phong(id = "m1", cd = c(0.95, 0.85, 0.1), ka = 0.3, kd = 0.7)
                phong(id = "m2", cd = c(0.9, 0.2, 0.2), ka = 0.3, kd = 0.7)
                matte(id = "m3", cd = c(0.2, 0.7, 0.3), ka = 0.3, kd = 0.7)
                matte(id = "m4", cd = c(0.2, 0.4, 0.85), ka = 0.3, kd = 0.7)
                phong(id = "m5", cd = c(0.8, 0.2, 0.7), ka = 0.3, kd = 0.7)
                phong(id = "m6", cd = c(0.1, 0.7, 0.75), ka = 0.3, kd = 0.7)
            }

            objects {
                plane(material = "ground", point = Point3D.ORIGIN, normal = Normal.UP)

                // center-based builders: positioned directly
                annulus(material = "m1", center = p(-6.0, 0.02, 0.0), innerRadius = 0.5, outerRadius = 1.2)
                partAnnulus(
                    material = "m2",
                    center = p(-3.6, 0.02, 0.0),
                    innerRadius = 0.5,
                    outerRadius = 1.2,
                    phiMax = 1.5 * PI,
                )
                partSphere(
                    material = "m3",
                    center = p(-1.2, 1.0, 0.0),
                    radius = 1.0,
                    phiMax = 1.5 * PI,
                )

                // origin-axis objects: placed via instance + translate
                instance(material = "m4", of = PartCylinder(0.0, 2.0, 1.0, 0.0, PI)) {
                    translate(v(1.2, 0.0, 0.0))
                }
                instance(material = "m5", of = PartTorus(1.0, 0.35, 0.0, 1.5 * PI)) {
                    translate(v(3.6, 0.5, 0.0))
                }
                instance(material = "m6", of = OpenCone(2.0, 1.0)) {
                    translate(v(6.0, 0.0, 0.0))
                }
            }
        }
}
