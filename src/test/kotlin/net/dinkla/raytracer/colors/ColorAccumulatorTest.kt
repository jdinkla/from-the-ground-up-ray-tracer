package net.dinkla.raytracer.colors

import net.dinkla.raytracer.colors.Color.Companion.GREEN
import net.dinkla.raytracer.colors.Color.Companion.RED
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class ColorAccumulatorTest {

    @Test
    fun `initial average value is black`() {
        val a = ColorAccumulator()
        assertEquals(Color.BLACK, a.average)
    }

    @Test
    fun `adding one changes average`() {
        val a = ColorAccumulator()
        a.plus(RED)
        assertEquals(RED, a.average)
    }

    @Test
    fun `adding two changes average`() {
        val a = ColorAccumulator()
        a.plus(RED)
        a.plus(GREEN)
        assertEquals(0.5 * (RED + GREEN), a.average)
    }
}