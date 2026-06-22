package net.dinkla.raytracer.cameras

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.shouldBeApprox

class StereoCameraTest :
    StringSpec({
        // Eye at the origin looking down -z with up = +y. For this frame Basis.create yields the
        // canonical axes, so the right axis u is exactly (1, 0, 0): the eyes shift along world +/-x.
        val eye = Point3D.ORIGIN
        val lookAt = Point3D(0.0, 0.0, -1.0)
        val up = Vector3D.UP
        val separation = 4.0 // half-separation = 2 -> eyes at x = -+2

        "left eye is the base eye shifted by -u*(separation/2)" {
            val stereo = StereoCamera(eye, lookAt, up, separation = separation)

            stereo.leftEye shouldBeApprox Point3D(-2.0, 0.0, 0.0)
        }

        "right eye is the base eye shifted by +u*(separation/2)" {
            val stereo = StereoCamera(eye, lookAt, up, separation = separation)

            stereo.rightEye shouldBeApprox Point3D(2.0, 0.0, 0.0)
        }

        "the right axis used for the offset is the base camera basis u" {
            val stereo = StereoCamera(eye, lookAt, up, separation = separation)

            stereo.baseBasis.u shouldBe Vector3D(1.0, 0.0, 0.0)
        }

        "parallel mode shifts each look-at by the same amount as its eye so view directions stay parallel" {
            val stereo = StereoCamera(eye, lookAt, up, separation = separation, mode = StereoMode.PARALLEL)

            stereo.leftLookAt shouldBeApprox Point3D(-2.0, 0.0, -1.0)
            stereo.rightLookAt shouldBeApprox Point3D(2.0, 0.0, -1.0)
            // Direction = lookAt - eye is identical for both eyes -> the views are parallel. Exact by
            // construction (the +/-x offsets cancel), so a plain equality check is appropriate.
            val leftDirection = stereo.leftLookAt - stereo.leftEye
            val rightDirection = stereo.rightLookAt - stereo.rightEye
            leftDirection shouldBe Vector3D(0.0, 0.0, -1.0)
            rightDirection shouldBe leftDirection
        }

        "transverse mode aims both eyes at the shared look-at (toed-in convergence)" {
            val stereo = StereoCamera(eye, lookAt, up, separation = separation, mode = StereoMode.TRANSVERSE)

            stereo.leftLookAt shouldBeApprox lookAt
            stereo.rightLookAt shouldBeApprox lookAt
        }

        "derived eye cameras carry the configured view-plane distance and offset eye positions" {
            val stereo = StereoCamera(eye, lookAt, up, separation = separation, d = 7.0)
            val vp = ViewPlane()

            val left = stereo.leftCamera(vp)
            val right = stereo.rightCamera(vp)

            left.eye shouldBeApprox Point3D(-2.0, 0.0, 0.0)
            right.eye shouldBeApprox Point3D(2.0, 0.0, 0.0)
            (left.lens as net.dinkla.raytracer.cameras.lenses.Pinhole).d shouldBe 7.0
            (right.lens as net.dinkla.raytracer.cameras.lenses.Pinhole).d shouldBe 7.0
        }
    })
