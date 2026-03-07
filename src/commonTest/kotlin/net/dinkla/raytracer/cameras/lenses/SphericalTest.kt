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

class SphericalTest :
    StringSpec({
        val eye = Point3D.ORIGIN
        val uvw = Basis.create(eye, Point3D(0.0, 0.0, -1.0), Vector3D.UP)
        val vp = ViewPlane()

        "getRaySingle always returns a ray" {
            val spherical = Spherical(vp, eye, uvw)
            val ray = spherical.getRaySingle(0, 0)
            ray shouldNotBe null
        }

        "getRaySingle has origin at eye" {
            val spherical = Spherical(vp, eye, uvw)
            val ray = spherical.getRaySingle(0, 0)
            ray.origin shouldBe eye
        }

        "getRaySingle at center produces a direction" {
            val spherical = Spherical(vp, eye, uvw)
            val midR = vp.resolution.height / 2
            val midC = vp.resolution.width / 2
            val ray = spherical.getRaySingle(midR, midC)
            ray.origin shouldBe eye
            ray.direction shouldNotBe Vector3D.ZERO
        }

        "getRaySampled always returns a ray" {
            val spherical = Spherical(vp, eye, uvw)
            val ray = spherical.getRaySampled(0, 0, Point2D(0.5, 0.5))
            ray shouldNotBe null
        }

        "getRaySampled has origin at eye" {
            val spherical = Spherical(vp, eye, uvw)
            val ray = spherical.getRaySampled(0, 0, Point2D(0.5, 0.5))
            ray.origin shouldBe eye
        }

        "getRaySampled with zero sample matches getRaySingle" {
            val spherical = Spherical(vp, eye, uvw)
            val raySingle = spherical.getRaySingle(5, 10)
            val raySampled = spherical.getRaySampled(5, 10, Point2D(0.0, 0.0))
            raySampled.direction.x shouldBeApprox raySingle.direction.x
            raySampled.direction.y shouldBeApprox raySingle.direction.y
            raySampled.direction.z shouldBeApprox raySingle.direction.z
        }

        "different pixels produce different ray directions" {
            val spherical = Spherical(vp, eye, uvw)
            val ray1 = spherical.getRaySingle(100, 200)
            val ray2 = spherical.getRaySingle(300, 400)
            (ray1.direction == ray2.direction) shouldBe false
        }

        "getRaySingle at extreme corners still returns a ray" {
            val spherical = Spherical(vp, eye, uvw)
            val ray = spherical.getRaySingle(vp.resolution.height - 1, vp.resolution.width - 1)
            ray shouldNotBe null
            ray.origin shouldBe eye
        }
    })
