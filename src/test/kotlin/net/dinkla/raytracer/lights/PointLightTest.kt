package net.dinkla.raytracer.lights

import net.dinkla.raytracer.colors.Color
import net.dinkla.raytracer.math.Point3D
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class PointLightTest {

    @Test
    fun `construct a point light with defaults for ls and color`() {
        val location = Point3D(1, 2, 3)
        val p = PointLight(location)
        assertEquals(1.0, p.ls)
        assertEquals(Color.WHITE, p.color)
        assertEquals(location, p.location)
    }

}