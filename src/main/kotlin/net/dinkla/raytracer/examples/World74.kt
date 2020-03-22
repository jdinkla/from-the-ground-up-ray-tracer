package net.dinkla.raytracer.examples

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.objects.acceleration.Acceleration
import net.dinkla.raytracer.objects.utilities.Ply
import net.dinkla.raytracer.world.Builder
import net.dinkla.raytracer.world.WorldDefinition

object World74 : WorldDefinition {

    override fun world() = Builder.build("World74") {

        // viewPlane(maxDepth= 2, numSamples= 4)

        //camera(d= 1000.0, eye= p(1.6, 1.0, 3.0), lookAt= p(1.6, 0.0, 0.0), numThreads= 32)
        camera(d = 1000.0, eye = p(1.6, 1.0, 3.0), lookAt = p(1.6, 0.0, 0.0))

        ambientLight(color = Color.WHITE, ls = 0.5)

        lights {
            pointLight(location = p(-1, 2, 3), color = c(1.0, 1.0, 1.0), ls = 1.0)
        }

        materials {
            phong(id = "grey", ks = 1.0, cd = c(0.1, 0.1, 0.1), ka = 0.5, kd = 1.0, exp = 10.0)
            matte(id = "sky", cd = c(0.1, 0.7, 1.0), ka = 0.75, kd = 1.0)
            reflective(id = "white", ks = 0.7, cd = c(1.0, 1.0, 1.0), ka = 0.5, kd = 0.75, exp = 2.0)
            phong(id = "red", ks = 0.7, cd = c(0.95, 0.2, 0.05), ka = 0.5, kd = 0.7, exp = 10.0)
            phong(id = "green", ks = 0.7, cd = c(0.2, 0.95, 0.05), ka = 0.5, kd = 0.7, exp = 20.0)
            phong(id = "blue", ks = 0.7, cd = c(0.2, 0.05, 0.95), ka = 0.5, kd = 0.7, exp = 30.0)
            phong(id = "yellow", ks = 0.9, cd = c(1.0, 0.95, 0.25), ka = 0.5, kd = 0.7, exp = 10.0)
        }

        objects {
            //plane(point= p(0,0,0), normal= n(0, 1, 0), material= "white")

            val green = this.materials["green"]!!
//            val ply1 = Ply.fromFile(fileName = "${path}/bunny/Bunny4K.ply", multiplier = 2.0, isSmooth = true, type = Acceleration.GRID, material = green)
            val ply1 = Ply.fromFile(fileName = "C:\\workspace\\ply\\bunny\\reconstruction\\bun_zipper_res4.ply", isSmooth = true, type = Acceleration.GRID, material = green)
            val ply1b = Ply.fromFile(fileName = "C:\\workspace\\ply\\bunny\\reconstruction\\bun_zipper_res3.ply", isSmooth = true, type = Acceleration.GRID, material = green)
            val ply1c = Ply.fromFile(fileName = "C:\\workspace\\ply\\bunny\\reconstruction\\bun_zipper_res2.ply", isSmooth = true, type = Acceleration.GRID, material = green)
            val ply2 = Ply.fromFile(fileName = "C:\\workspace\\ply\\bunny\\reconstruction\\bun_zipper_res4.ply", isSmooth = true, type = Acceleration.KDTREE, material = green)

            instance(of = ply1.compound, material = "green") {
                scale(v(3.0, 3.0, 3.0))
                rotateY(10.0)
                translate(v(1, 0, 0))
            }

            instance(of = ply1b.compound, material = "green") {
                scale(v(3.0, 3.0, 3.0))
                rotateY(10.0)
                translate(v(2, 0, 0))
            }

            instance(of = ply1c.compound, material = "green") {
                scale(v(3.0, 3.0, 3.0))
                rotateY(10.0)
                translate(v(3, 0, 0))
            }

//            instance(of = ply2.compound, material = "green") {
//                scale(v(3.0, 3.0, 3.0))
//                rotateY(10.0)
//                translate(v(2, 0, 0))
//            }

        }

    }
}
