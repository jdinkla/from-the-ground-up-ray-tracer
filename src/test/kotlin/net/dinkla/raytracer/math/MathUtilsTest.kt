package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class MathUtilsTest {

    @Test
    fun `min for 3 doubles`() {
        assertEquals(1.0, MathUtils.min(1.0, 2.0, 3.0))
        assertEquals(1.0, MathUtils.min(1.0, 3.0, 2.0))
        assertEquals(1.0, MathUtils.min(2.0, 1.0, 3.0))
        assertEquals(1.0, MathUtils.min(2.0, 3.0, 1.0))
        assertEquals(1.0, MathUtils.min(3.0, 1.0, 2.0))
        assertEquals(1.0, MathUtils.min(3.0, 2.0, 1.0))
    }

    @Test
    fun `max for 3 doubles`() {
        assertEquals(3.0, MathUtils.max(1.0, 2.0, 3.0))
        assertEquals(3.0, MathUtils.max(1.0, 3.0, 2.0))
        assertEquals(3.0, MathUtils.max(2.0, 1.0, 3.0))
        assertEquals(3.0, MathUtils.max(2.0, 3.0, 1.0))
        assertEquals(3.0, MathUtils.max(3.0, 1.0, 2.0))
        assertEquals(3.0, MathUtils.max(3.0, 2.0, 1.0))
    }

}