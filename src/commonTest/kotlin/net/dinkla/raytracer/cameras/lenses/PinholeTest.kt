package net.dinkla.raytracer.cameras.lenses

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

class PinholeTest :
    StringSpec({
        val eye = Point3D.ORIGIN
        val uvw = Basis.create(eye, Point3D(0.0, 0.0, -1.0), Vector3D.UP)
        val vp = ViewPlane()

        "getRaySingle returns ray with origin at eye" {
            val pinhole = Pinhole(vp, eye, uvw)
            val ray = pinhole.getRaySingle(0, 0)
            ray.origin shouldBe eye
        }

        "getRaySingle returns non-null ray" {
            val pinhole = Pinhole(vp, eye, uvw)
            val ray = pinhole.getRaySingle(0, 0)
            ray shouldNotBe null
        }

        "getRaySingle ray direction is normalized" {
            val pinhole = Pinhole(vp, eye, uvw)
            val ray = pinhole.getRaySingle(0, 0)
            ray.direction.length shouldBeApprox 1.0
        }

        "getRaySingle center pixel looks roughly forward" {
            val pinhole = Pinhole(vp, eye, uvw)
            val midR = vp.resolution.height / 2
            val midC = vp.resolution.width / 2
            val ray = pinhole.getRaySingle(midR, midC)
            ray.origin shouldBe eye
            ray.direction.length shouldBeApprox 1.0
        }

        "getRaySampled returns ray with origin at eye" {
            val pinhole = Pinhole(vp, eye, uvw)
            val ray = pinhole.getRaySampled(0, 0, Point2D(0.5, 0.5))
            ray.origin shouldBe eye
        }

        "getRaySampled ray direction is normalized" {
            val pinhole = Pinhole(vp, eye, uvw)
            val ray = pinhole.getRaySampled(0, 0, Point2D(0.5, 0.5))
            ray.direction.length shouldBeApprox 1.0
        }

        "getRaySampled with zero sample matches getRaySingle" {
            val pinhole = Pinhole(vp, eye, uvw)
            val raySingle = pinhole.getRaySingle(5, 10)
            val raySampled = pinhole.getRaySampled(5, 10, Point2D(0.0, 0.0))
            raySampled.direction.x shouldBeApprox raySingle.direction.x
            raySampled.direction.y shouldBeApprox raySingle.direction.y
            raySampled.direction.z shouldBeApprox raySingle.direction.z
        }

        "different d values produce different ray directions" {
            val pinhole1 = Pinhole(vp, eye, uvw).apply { d = 1.0 }
            val pinhole2 = Pinhole(vp, eye, uvw).apply { d = 5.0 }
            val ray1 = pinhole1.getRaySingle(100, 100)
            val ray2 = pinhole2.getRaySingle(100, 100)
            // Different focal distances should produce different directions for off-center pixels
            (ray1.direction == ray2.direction) shouldBe false
        }

        "symmetric pixels produce mirrored ray directions" {
            val pinhole = Pinhole(vp, eye, uvw)
            val midR = vp.resolution.height / 2
            val midC = vp.resolution.width / 2
            val rayLeft = pinhole.getRaySingle(midR, midC - 10)
            val rayRight = pinhole.getRaySingle(midR, midC + 10)
            // x components should be opposite in sign
            rayLeft.direction.x shouldBeApprox -rayRight.direction.x
        }
    })
