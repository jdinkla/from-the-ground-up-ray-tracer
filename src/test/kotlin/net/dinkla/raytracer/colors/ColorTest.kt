package net.dinkla.raytracer.colors

import net.dinkla.raytracer.colors.Color.Companion.fromInt
import net.dinkla.raytracer.colors.Color.Companion.fromString
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
    fun `multiplication with scalar from left`() {
        assertEquals(Color(0.25), 2.5 * Color(0.1))
    }

    @Test
    fun pow() {
        assertEquals(Color(0.1*0.1, 0.2*0.2, 0.3*0.3), Color(0.1, 0.2, 0.3).pow(2.0))
    }

    @Test
    fun asInt() {
        val c = Color(0.0, 0.0, 1.0);
        assertEquals(255, c.toInt())
    }

    @Test
    fun createFromInt() {
        val r = 3.0 / 255.0
        val g = 31.0 / 255.0
        val b = 139.0 / 255.0
        val rgb = Color(r, g, b).toInt();
        val c = fromInt(rgb)

        assertEquals(r, c.red, 0.01)
        assertEquals(g, c.green, 0.01)
        assertEquals(b, c.blue, 0.01)
    }

    @Test
    fun createFromInts() {
        val color = Color.fromRGB(127, 0, 255)

        assertEquals(127.0/255.0, color.red, 0.01)
        assertEquals(0.0, color.green, 0.01)
        assertEquals(1.0, color.blue, 0.01)
    }

    @Test
    fun createFromString() {
        assertEquals(Color(1.0, 0.0, 0.0), fromString("FF0000"))
        assertEquals(Color(0.0, 1.0, 0.0), fromString("00FF00"))
        assertEquals(Color(0.0, 0.0, 1.0), fromString("0000FF"))
    }

    @Test
    fun clamp() {
        assertEquals(Color.CLAMP_COLOR, Color(1.1, 2.2, 3.3).clamp())
    }

    @Test
    fun `clamp should return input if not clamped`() {
        val c = Color(0.1, 0.2, 0.3)
        assertEquals(c, c.clamp())
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
        assertEquals(Color(0.5, 0.2, 0.3), Color(0.5, 0.2, 0.3))
        assertFalse(Color(0.5, 0.2, 0.3).equals(3.0))
        assertFalse(Color(0.5, 0.2, 0.3).equals(null))
    }
}