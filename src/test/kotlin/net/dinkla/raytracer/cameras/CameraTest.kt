package net.dinkla.raytracer.cameras

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldNotBe
import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.cameras.lenses.Pinhole
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.utilities.Resolution

class CameraTest : AnnotationSpec() {
    @Test
    fun testComputeUVW() {
        val lens = Pinhole(ViewPlane(Resolution.RESOLUTION_1080))
        val c = Camera(lens)
        c.setup(eye = Point3D.ORIGIN, lookAt = Point3D.ORIGIN, up = Vector3D.UP)
        c.uvw.u shouldNotBe null
        c.uvw.v shouldNotBe null
        c.uvw.w shouldNotBe null
    }
}
