package net.dinkla.raytracer.cameras.lenses

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D

class AbstractLensTest :
    StringSpec({
        val eye = Point3D.ORIGIN
        val uvw = Basis.create(eye, Point3D(0.0, 0.0, -1.0), Vector3D.UP)
        val vp = ViewPlane()

        "Pinhole is an AbstractLens" {
            val pinhole = Pinhole(vp, eye, uvw)
            pinhole.shouldBeInstanceOf<AbstractLens>()
        }

        "FishEye is an AbstractLens" {
            val fishEye = FishEye(vp, eye, uvw)
            fishEye.shouldBeInstanceOf<AbstractLens>()
        }

        "Spherical is an AbstractLens" {
            val spherical = Spherical(vp, eye, uvw)
            spherical.shouldBeInstanceOf<AbstractLens>()
        }

        "ThinLens is an AbstractLens" {
            val thinLens = ThinLens(vp, eye, uvw)
            thinLens.shouldBeInstanceOf<AbstractLens>()
        }

        "all lenses implement ILens" {
            val pinhole = Pinhole(vp, eye, uvw)
            pinhole.shouldBeInstanceOf<ILens>()
        }

        "viewPlane property is accessible" {
            val pinhole = Pinhole(vp, eye, uvw)
            pinhole.viewPlane shouldBe vp
        }

        "eye property is accessible" {
            val pinhole = Pinhole(vp, eye, uvw)
            pinhole.eye shouldBe eye
        }

        "uvw property is accessible" {
            val pinhole = Pinhole(vp, eye, uvw)
            pinhole.uvw shouldBe uvw
        }

        "OFFSET is 0.5" {
            AbstractLens.OFFSET shouldBe 0.5
        }
    })
