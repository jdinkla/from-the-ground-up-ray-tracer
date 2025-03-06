package net.dinkla.raytracer.examples.objects

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Axis
import net.dinkla.raytracer.objects.SmoothTriangle
import net.dinkla.raytracer.objects.Triangle
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition

object World31 : WorldDefinition {
    override val id: String = "World31.kt"

    override fun world(): World =
        Builder.build {
            val t1 = SmoothTriangle(p(-0.25, 0.0, 0.2), p(0.25, 0.1, -0.1), p(0.23, -0.2, +0.025))
            val t2 = SmoothTriangle(p(0, -1, 0), p(0.75, 0.4, -0.025), p(-0.05, 0.0, +0.025))

            val tr1 = Triangle(p(3.0, 0.0, 0.2), p(3.2, 0.2, -0.2), p(2.9, -0.3, +0.025))
            val tr2 = Triangle(p(3.1, 0.0, 0.0), p(3.15, 0.3, -0.21), p(2.8, -0.33, +0.125))

            camera(d = 400.0, lookAt = p(2, 0, 0), eye = p(0.0, 0.1, 2.0))

            ambientLight(ls = 0.5)

            lights {
                pointLight(location = p(0, 10, 0), ls = 1.0)
            }

            materials {
                matte(id = "grey", cd = c(0.4), ka = 0.25, kd = 0.75)
                matte(id = "rm", cd = Color.RED, ka = 1.0, kd = 1.0)
                matte(id = "gm", cd = Color.GREEN, ka = 1.0, kd = 1.0)
                matte(id = "bm", cd = Color.BLUE, ka = 1.0, kd = 1.0)
                phong(id = "r", ks = 1.0, cd = Color.RED, ka = 1.0, kd = 1.0, exp = 10.0)
                phong(id = "g", ks = 1.0, cd = Color.GREEN, ka = 1.0, kd = 1.0, exp = 20.0)
                phong(id = "b", ks = 1.0, cd = Color.BLUE, ka = 1.0, kd = 1.0, exp = 20.0)
                reflective(id = "rr", ks = 1.0, cd = Color.RED, ka = 1.0, kd = 1.0, exp = 10.0)
                reflective(id = "gr", ks = 1.0, cd = Color.GREEN, ka = 1.0, kd = 1.0, exp = 20.0)
                reflective(id = "br", ks = 1.0, cd = Color.BLUE, ka = 1.0, kd = 1.0, exp = 20.0)
            }

            objects {
                plane(material = "grey", point = p(0, 0, -2), normal = n(0, 0, 1))

                sphere(material = "b", center = p(3, 0, 0), radius = 0.1)
                sphere(material = "r", center = p(3.09, 0.1, 0.0), radius = 0.1)
                sphere(material = "g", center = p(3.04, 0.1, -0.09), radius = 0.1)

                instance(material = "bm", of = tr1) {
                    rotate(Axis.X, 10.0)
                }

                instance(material = "rm", of = tr2) {
                    rotate(Axis.X, 20.0)
                }

                instance(material = "bm", of = tr1) {
                    rotate(Axis.X, 10.0)
                }

                instance(material = "rm", of = tr2) {
                    rotate(Axis.X, 20.0)
                }

                instance(material = "r", of = t1) {}

                instance(material = "g", of = t1) {
                    rotate(Axis.Z, 120.0)
                }

                instance(material = "b", of = t1) {
                    rotate(Axis.Z, 240.0)
                }

                instance(material = "r", of = t2) {
                    translate(v(2, 0, 0))
                }

                instance(material = "g", of = t2) {
                    rotate(Axis.Z, 20.0)
                    translate(v(2, 0, 0))
                }

                instance(material = "b", of = t2) {
                    rotate(Axis.Z, 4.0)
                    translate(v(2, 0, 0))
                }
            }
        }
}
