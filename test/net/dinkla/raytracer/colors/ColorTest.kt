package net.dinkla.raytracer.colors

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ColorTest {

    @Test
    fun plus() {
        assertEquals(Color(0.2, 0.4, 0.5), Color(0.1) + Color(0.1, 0.3, 0.4))
    }

    @Test
    fun `multiplication of two colors`() {
        assertEquals(Color(0.25), Color(0.5) * Color(0.5) )
    }

    @Test
    fun `multiplication with scalar`() {
        assertEquals(Color(0.25), Color(0.1) * 2.5)
    }

    @Test
    fun pow() {
        assertEquals(Color(0.1*0.1, 0.2*0.2, 0.3*0.3), Color(0.1, 0.2, 0.3).pow(2.0))

    }

    @Test
    fun asInt() {
        val c = Color(0.0, 0.0, 1.0);
        assertEquals(255, c.asInt())
    }

    @Test
    fun createFromInt() {
        val r = 3.0 / 255.0
        val g = 31.0 / 255.0
        val b = 139.0 / 255.0
        val rgb = Color(r, g, b).asInt();
        val c = Color.createFromInt(rgb)

        assertEquals(r, c.red, 0.01)
        assertEquals(g, c.green, 0.01)
        assertEquals(b, c.blue, 0.01)
    }

    @Test
    fun clamp() {
        assertEquals(Color.CLAMP_COLOR, Color(1.1, 2.2, 3.3).clamp())
    }

    @Test
    fun maxToOne() {
        assertEquals(Color(1.0), Color(1.0).maxToOne())
        assertEquals(Color(0.25, 0.25, 1.0), Color(0.5, 0.5, 2.0).maxToOne())
        assertEquals(Color(0.25, 1.0, 0.25), Color(0.5, 2.0, 0.5).maxToOne())
        assertEquals(Color(1.0, 0.25, 0.25), Color(2.0, 0.5, 0.5).maxToOne())
    }

    @Test
    fun equals() {
        assertTrue(Color(0.5, 0.2, 0.3) == Color(0.5, 0.2, 0.3))
        assertFalse(Color(0.5, 0.2, 0.3).equals(3.0))
    }
}