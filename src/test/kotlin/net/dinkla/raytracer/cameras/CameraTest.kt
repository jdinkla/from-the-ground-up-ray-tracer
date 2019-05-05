package net.dinkla.raytracer.cameras

import net.dinkla.raytracer.ViewPlane
import net.dinkla.raytracer.cameras.lenses.Pinhole
import net.dinkla.raytracer.math.Point3D
import net.dinkla.raytracer.math.Vector3D
import net.dinkla.raytracer.utilities.Resolution
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.Test

class CameraTest {
    @Test
    @Throws(Exception::class)
    fun testComputeUVW() {
        val lens = Pinhole(ViewPlane(Resolution.RESOLUTION_1080))
        val c = Camera(lens).apply {
            eye = Point3D.ORIGIN
            lookAt = Point3D.ORIGIN
            up = Vector3D.UP
        }
        assertNotNull(c.uvw.u)
        assertNotNull(c.uvw.v)
        assertNotNull(c.uvw.w)
    }
}
