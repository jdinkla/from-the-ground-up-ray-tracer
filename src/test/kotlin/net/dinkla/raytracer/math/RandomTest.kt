package net.dinkla.raytracer.math


import org.junit.jupiter.api.Test

import java.util.ArrayList

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertTrue

class RandomTest {

    internal val NUM = 1000

    @Test
    fun randInt() {
        for (i in 0 until NUM) {
            val r = Random.int(10)
            assert(0 <= r)
            assert(r < 10)
        }

        for (i in 0 until NUM) {
            val r = Random.int(18, 44)
            assert(18 <= r)
            assert(r < 44)
        }
    }

    @Test
    fun randFloat() {
        for (i in 0 until NUM) {
            val r = Random.double()
            assert(0.0 <= r)
            assert(r < 1.0)
        }
    }

    @Test
    fun randomShuffle() {
        val ls = ArrayList<Int>()

        assertEquals(0, ls.size)
        Random.randomShuffle(ls)
        assertEquals(0, ls.size)

        ls.add(11)
        ls.add(12)
        ls.add(13)

        assertEquals(3, ls.size)

        val i0 = Histogram()
        val i1 = Histogram()
        val i2 = Histogram()

        for (i in 0 until NUM) {
            Random.randomShuffle(ls)
            assertEquals(3, ls.size)
            assertTrue(11 == ls[0] || 11 == ls[1] || 11 == ls[2])
            assertTrue(12 == ls[0] || 12 == ls[1] || 12 == ls[2])
            assertTrue(13 == ls[0] || 13 == ls[1] || 13 == ls[2])
            i0.add(ls[0])
            i1.add(ls[1])
            i2.add(ls[2])
        }

        assertEquals(3, i0.keys().size)
        assertEquals(3, i1.keys().size)
        assertEquals(3, i2.keys().size)
    }

}
