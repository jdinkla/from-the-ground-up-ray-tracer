package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.beveled.BeveledCylinder
import net.dinkla.raytracer.objects.beveled.BeveledWedge
import net.dinkla.raytracer.objects.compound.Bowl
import net.dinkla.raytracer.objects.compound.SolidCone
import net.dinkla.raytracer.objects.compound.ThickRing
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition
import kotlin.math.PI

/**
 * Coverage example (TASK-40) for the beveled/compound solids: [BeveledCylinder], [BeveledWedge],
 * [ThickRing], [SolidCone] and [Bowl] (all origin-axis, placed across the row via
 * `instance { translate(...) }`), plus a large [net.dinkla.raytracer.objects.ConcaveSphere] used in
 * its idiomatic role as an enclosing sky-dome (inward-facing normals) so the scene has a lit backdrop
 * instead of a black void.
 */
object SolidsAndBeveled : WorldDefinition {
    override val id: String = "SolidsAndBeveled.kt"

    override fun world() =
        Builder.build {
            camera(d = 1600.0, eye = p(0.0, 5.0, 14.0), lookAt = p(0.0, 0.8, 0.0))

            ambientLight(color = Color.WHITE, ls = 0.5)

            lights {
                pointLight(location = p(-6, 8, 12), ls = 3.0)
                pointLight(location = p(8, 6, 10), ls = 2.0, color = c(1.0, 0.9, 0.8))
            }

            materials {
                matte(id = "ground", cd = c(0.7, 0.7, 0.7), ka = 0.3, kd = 0.7)
                matte(id = "sky", cd = c(0.55, 0.7, 0.9), ka = 0.9, kd = 0.4)
                reflective(id = "metal", cd = c(0.8, 0.8, 0.85), ka = 0.3, kd = 0.4, ks = 0.6, kr = 0.6)
                phong(id = "m2", cd = c(0.9, 0.4, 0.15), ka = 0.3, kd = 0.7)
                phong(id = "m3", cd = c(0.3, 0.6, 0.85), ka = 0.3, kd = 0.7)
                phong(id = "m4", cd = c(0.85, 0.75, 0.1), ka = 0.3, kd = 0.7)
                matte(id = "m5", cd = c(0.75, 0.2, 0.45), ka = 0.3, kd = 0.7)
            }

            objects {
                plane(material = "ground", point = Point3D.ORIGIN, normal = Normal.UP)

                // idiomatic concave sphere: a big inward-facing dome enclosing the scene
                concaveSphere(material = "sky", center = p(0.0, 0.0, 0.0), radius = 40.0)

                instance(material = "metal", of = BeveledCylinder(0.0, 2.0, 1.0, 0.2)) {
                    translate(v(-6.0, 0.0, 0.0))
                }
                instance(material = "m2", of = BeveledWedge(0.0, 2.0, 0.6, 1.2, 0.0, PI / 2.0, 0.15)) {
                    translate(v(-3.6, 0.0, 0.0))
                }
                instance(material = "m3", of = ThickRing(0.0, 0.8, 0.6, 1.2)) {
                    translate(v(-1.2, 0.0, 0.0))
                }
                instance(material = "m4", of = SolidCone(2.2, 1.0)) {
                    translate(v(1.2, 0.0, 0.0))
                }
                instance(material = "m5", of = Bowl(0.85, 1.0)) {
                    translate(v(3.6, 1.0, 0.0))
                }
            }
        }
}
