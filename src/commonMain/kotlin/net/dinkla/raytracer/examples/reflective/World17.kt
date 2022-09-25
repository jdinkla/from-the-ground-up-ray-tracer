package net.dinkla.raytracer.examples.reflective

import net.dinkla.raytracer.math.Normal
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.objects.AlignedBox
import net.dinkla.raytracer.objects.OpenCylinder
import net.dinkla.raytracer.objects.compound.SolidCylinder
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.World
import net.dinkla.raytracer.world.WorldDefinition


object World17 : WorldDefinition {

    override val id: String = "World17.kt"

    override fun world(): World = Builder.build {

//        viewPlane(numSamples = 0, maxDepth = 5)
        //camera(direction= 800, eye= p(0, 2.5, 7), lookAt= p(1, 0.5, 1))
//    camera(direction= 800, eye= p(0, 2.5, 15), lookAt= p(1.5, 0.5, 1))
//    camera(direction= 800, eye= p(0, 12.5, 15), lookAt= p(1.5, 0.5, 1))
//    camera(direction= 800, eye= p(0, 1.1, 15), lookAt= p(1.5, 0.5, 1), numThreads= 30)
//    camera(direction= 800, eye= p(0, 250, 15), lookAt= p(1.5, 0.5, 1), type= PinholePar, numThreads= 30)
//        camera(d = 800.0, eye = p(0.0, 2.1, 15.0), lookAt = p(1.5, 0.5, 1.0), numThreads = 16)
        camera(d = 1800.0, eye = p(0.0, 2.1, 15.0), lookAt = p(1.5, 0.5, 1.0))

        ambientLight(ls = 0.5)

        lights {
            pointLight(location = p(-6, 1, 2), ls = 1.0)
            pointLight(location = p(4, 2, 7), ls = 1.0, color = c(1.0, 0.9, 0.9))
        }

        materials {
            phong(id = "plane", cd = c(1.0), ka = 0.75, kd = 0.75)
            reflective(id = "m1", cd = c(1.0, 1.0, 0.0), ka = 0.75, kd = 0.75, exp = 10.0, ks = 1.0, kr = 1.0)
            reflective(id = "p1", cd = c(1.0, 1.0, 0.0), ka = 0.75, kd = 0.75, exp = 10.0, ks = 1.0, kr = 1.0)
            reflective(id = "m3", cd = c(0.0, 0.0, 1.0), ka = 0.25, kd = 0.5, kr = 1.0)
            reflective(id = "p2", cd = c(1.0, 0.0, 1.0), ka = 0.75, kd = 0.75, exp = 10.0, ks = 1.0, kr = 1.0)
/*
        phong(id= "m2", cd= c(1), ka= 0.75f, kd= 0.75f, kr= 1)
        phong(id= "m1", cd= c(1, 1, 0), ka= 0.75f, kd= 0.75f, exp= 10, ks= 1.0, kr= 1)
        phong(id= "p1", cd= c(1, 1, 0), ka= 0.75f, kd= 0.75f, exp= 10, ks= 1.0, kr= 1)
        phong(id= "m3", cd= c(0, 0, 1), ka= 0.25f, kd= 0.5, kr= 1)
        phong(id= "p2", cd= c(1, 0, 1), ka= 0.75f, kd= 0.75f, exp= 10, ks= 1.0, kr= 1)
*/
        }

        objects {
            plane(material = "plane", point = Point3D.ORIGIN, normal = Normal.UP)
            solidCylinder(material = "m1", y0 = 0.0, y1 = 3.5, radius = 0.9)
            alignedBox(material = "m3", p = p(2, 0, -2), q = p(3.0, 2.3, 3.0))

            val o1 = OpenCylinder(y0= 0.0, y1 = 3.5, radius = 0.9).apply { this.material = materials["m1"]}
            val s1 = SolidCylinder(y0= 0.0, y1 = 3.5, radius = 0.9).apply { this.material = materials["m1"]}
            val a1 = AlignedBox(p = p(2, 0, -2), q = p(2.1, 3.3, 5.0)).apply { this.material = materials["m1"]}

            instance(material = "m3", of = a1) {
                translate(v(2, 0, 0))
            }

            instance(material = "m1", of = s1) {
                translate(v(-2, 0, -2))
            }

            instance(material = "m1", of = o1) {
                translate(v(-4, 0, -1))
            }

            sphere(center = p(8.0, 0.5, 0.0), radius = 0.5, material = "m1")

            alignedBox(material = "p2", p = p(-4.0, 0.5, 20.0), q = p(120.0, 1.2, 21.0))
        }
    }
}