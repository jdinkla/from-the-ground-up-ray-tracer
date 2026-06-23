package net.dinkla.raytracer.cameras.lenses

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.nulls.shouldBeNull
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Basis
import net.dinkla.raytracer.math.Point2D
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

class FishEyeTest :
    StringSpec({
        val eye = Point3D.ORIGIN
        val uvw = Basis.create(eye, Point3D(0.0, 0.0, -1.0), Vector3D.UP)
        val vp = ViewPlane()

        "getRaySingle at center returns non-null ray" {
            val fishEye = FishEye(vp, eye, uvw)
            val midR = vp.resolution.height / 2
            val midC = vp.resolution.width / 2
            val ray = fishEye.getRaySingle(midR, midC)
            ray.shouldNotBeNull()
        }

        "getRaySingle at center has origin at eye" {
            val fishEye = FishEye(vp, eye, uvw)
            val midR = vp.resolution.height / 2
            val midC = vp.resolution.width / 2
            val ray = fishEye.getRaySingle(midR, midC)
            ray.shouldNotBeNull()
            ray.origin shouldBe eye
        }

        "getRaySingle at center produces normalized direction" {
            val fishEye = FishEye(vp, eye, uvw)
            val midR = vp.resolution.height / 2
            val midC = vp.resolution.width / 2
            val ray = fishEye.getRaySingle(midR, midC)
            ray.shouldNotBeNull()
            ray.direction.length shouldBeApprox 1.0
        }

        "getRaySingle far from center returns null (rSquared > 1)" {
            val fishEye = FishEye(vp, eye, uvw)
            // Extreme corners should be outside the unit circle
            val ray = fishEye.getRaySingle(0, 0)
            ray.shouldBeNull()
        }

        "getRaySampled at center returns non-null ray" {
            val fishEye = FishEye(vp, eye, uvw)
            val midR = vp.resolution.height / 2
            val midC = vp.resolution.width / 2
            val ray = fishEye.getRaySampled(midR, midC, Point2D(0.0, 0.0))
            ray.shouldNotBeNull()
        }

        "getRaySampled far from center returns null" {
            val fishEye = FishEye(vp, eye, uvw)
            val ray = fishEye.getRaySampled(0, 0, Point2D(0.0, 0.0))
            ray.shouldBeNull()
        }

        "getRaySampled with zero sample matches getRaySingle at center" {
            val fishEye = FishEye(vp, eye, uvw)
            val midR = vp.resolution.height / 2
            val midC = vp.resolution.width / 2
            val raySingle = fishEye.getRaySingle(midR, midC)
            val raySampled = fishEye.getRaySampled(midR, midC, Point2D(0.0, 0.0))
            raySingle.shouldNotBeNull()
            raySampled.shouldNotBeNull()
            raySampled.direction.x shouldBeApprox raySingle.direction.x
            raySampled.direction.y shouldBeApprox raySingle.direction.y
            raySampled.direction.z shouldBeApprox raySingle.direction.z
        }

        // An off-centre pixel that is still inside the unit image circle exercises the `r != 0`
        // projection branch (the azimuth uses x/r and y/r), which the at-centre cases skip.
        "getRaySingle off-centre but inside the circle returns a normalized ray" {
            val fishEye = FishEye(vp, eye, uvw)
            val midR = vp.resolution.height / 2
            // Shift the column well off centre but still inside the circle (radius well below 1).
            val offC = vp.resolution.width / 2 + 100
            val ray = fishEye.getRaySingle(midR, offC)
            ray.shouldNotBeNull()
            ray.origin shouldBe eye
            ray.direction.length shouldBeApprox 1.0
        }

        "getRaySampled off-centre but inside the circle returns a normalized ray" {
            val fishEye = FishEye(vp, eye, uvw)
            val midR = vp.resolution.height / 2
            val offC = vp.resolution.width / 2 + 100
            val ray = fishEye.getRaySampled(midR, offC, Point2D(0.0, 0.0))
            ray.shouldNotBeNull()
            ray.direction.length shouldBeApprox 1.0
        }
    })
