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

class ThinLensTest :
    StringSpec({
        val eye = Point3D.ORIGIN
        val uvw = Basis.create(eye, Point3D(0.0, 0.0, -1.0), Vector3D.UP)
        val vp = ViewPlane()

        "getRaySingle returns ray with origin at eye" {
            val thinLens = ThinLens(vp, eye, uvw)
            val ray = thinLens.getRaySingle(0, 0)
            ray.origin shouldBe eye
        }

        "getRaySingle returns normalized direction" {
            val thinLens = ThinLens(vp, eye, uvw)
            val ray = thinLens.getRaySingle(0, 0)
            ray.direction.length shouldBeApprox 1.0
        }

        "getRaySampled returns ray with origin at eye" {
            val thinLens = ThinLens(vp, eye, uvw)
            val ray = thinLens.getRaySampled(0, 0, Point2D(0.5, 0.5))
            ray.origin shouldBe eye
        }

        "getRaySampled returns normalized direction" {
            val thinLens = ThinLens(vp, eye, uvw)
            val ray = thinLens.getRaySampled(0, 0, Point2D(0.5, 0.5))
            ray.direction.length shouldBeApprox 1.0
        }

        "getRaySingle and getRaySampled return same direction" {
            val thinLens = ThinLens(vp, eye, uvw)
            val raySingle = thinLens.getRaySingle(5, 10)
            val raySampled = thinLens.getRaySampled(5, 10, Point2D(0.3, 0.7))
            raySingle.direction.x shouldBeApprox raySampled.direction.x
            raySingle.direction.y shouldBeApprox raySampled.direction.y
            raySingle.direction.z shouldBeApprox raySampled.direction.z
        }

        "direction is independent of pixel coordinates" {
            val thinLens = ThinLens(vp, eye, uvw)
            val ray1 = thinLens.getRaySingle(0, 0)
            val ray2 = thinLens.getRaySingle(100, 200)
            ray1.direction.x shouldBeApprox ray2.direction.x
            ray1.direction.y shouldBeApprox ray2.direction.y
            ray1.direction.z shouldBeApprox ray2.direction.z
        }

        "default d and f are 1.0" {
            val thinLens = ThinLens(vp, eye, uvw)
            thinLens.d shouldBe 1.0
            thinLens.f shouldBe 1.0
        }

        "sampler defaults to null" {
            val thinLens = ThinLens(vp, eye, uvw)
            thinLens.sampler shouldBe null
        }

        "direction is non-zero" {
            val thinLens = ThinLens(vp, eye, uvw)
            val ray = thinLens.getRaySingle(0, 0)
            ray.direction shouldNotBe Vector3D.ZERO
        }
    })
