package net.dinkla.raytracer.cameras

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.cameras.lenses.Pinhole
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D

class CameraTest :
    StringSpec({
        "computes an orthonormal UVW basis from eye, lookAt and up" {
            // Eye on +z looking at the origin, up = +y -> the canonical world axes.
            val c =
                Camera(
                    { eye, uvw -> Pinhole(ViewPlane(), eye, uvw) },
                    eye = Point3D(0.0, 0.0, 10.0),
                    lookAt = Point3D.ORIGIN,
                    up = Vector3D.UP,
                )
            // w points from lookAt back toward the eye; u, v complete a right-handed frame.
            c.uvw.u shouldBe Vector3D(1.0, 0.0, 0.0)
            c.uvw.v shouldBe Vector3D(0.0, 1.0, 0.0)
            c.uvw.w shouldBe Vector3D(0.0, 0.0, 1.0)
        }

        "defaults exposureTime to 1.0 so existing scenes are unaffected" {
            val c = Camera({ eye, uvw -> Pinhole(ViewPlane(), eye, uvw) })

            c.exposureTime shouldBe 1.0
        }
    })
