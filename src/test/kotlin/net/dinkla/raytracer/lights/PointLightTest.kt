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

    @Test
    fun `construct a point light without defaults`() {
        val location = Point3D(1, 2, 3)
        val ls = 0.12
        val color = Color(0.12, 0.23, 0.34)
        val p = PointLight(location, ls, color)
        assertEquals(ls, p.ls)
        assertEquals(color, p.color)
        assertEquals(location, p.location)
    }
}