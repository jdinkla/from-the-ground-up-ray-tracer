package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class HistogramTest {

    @Test
    fun `an empty histogram has no keys`() {
        val h = Histogram()
        assertEquals(0, h.counts.keys.size)
    }

    @Test
    fun `adding one element`() {
        val h = Histogram()
        h.add(3)
        assertEquals(1, h.counts.keys.size)
        assertEquals(1, h.get(3))
    }

    @Test
    fun `adding one element twice`() {
        val h = Histogram()
        h.add(3)
        h.add(3)
        assertEquals(1, h.counts.keys.size)
        assertEquals(2, h.get(3))
    }

    @Test
    fun `adding two different elements`() {
        val h = Histogram()
        h.add(3)
        h.add(4)
        assertEquals(2, h.counts.keys.size)
        assertEquals(1, h.get(3))
        assertEquals(1, h.get(4))
    }

    @Test
    fun `adding multiple different elements`() {
        val h = Histogram()
        h.add(3)
        h.add(4)
        h.add(3)
        h.add(4)
        h.add(3)
        assertEquals(2, h.counts.keys.size)
        assertEquals(3, h.get(3))
        assertEquals(2, h.get(4))
    }

    @Test
    fun `keys returns the keys`() {
        val h = Histogram()
        h.add(3)
        assertEquals(1, h.keys().size)
        assertEquals(setOf(3), h.keys())
    }
}