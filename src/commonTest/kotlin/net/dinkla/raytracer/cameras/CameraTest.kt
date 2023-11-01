package net.dinkla.raytracer.cameras

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldNotBe
import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.cameras.lenses.Pinhole
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D

class CameraTest : StringSpec({
    "testComputeUVW" {
        val c = Camera({ eye, uvw -> Pinhole(ViewPlane(), eye, uvw) }, Point3D.ORIGIN, Point3D.ORIGIN, Vector3D.UP)
        c.uvw.u shouldNotBe null
        c.uvw.v shouldNotBe null
        c.uvw.w shouldNotBe null
    }
})
