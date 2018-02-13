package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals

class WrappedFloatTest {

    @Test
    fun testMethodParameter() {
        val f = WrappedFloat(1.23)
        //        set(f);
        assert(f.value == 1.23)
    }

    @Test
    fun testComparable() {
        val f1 = WrappedFloat(0.0)
        val f2 = WrappedFloat(0.0)
        val f3 = WrappedFloat(1.0)
        val f4 = WrappedFloat(2.0)
        assertEquals(f1, f2)
        assertEquals(f3.compareTo(f4), -1)
        assertEquals(f1.compareTo(f4), -1)
        assertEquals(f2.compareTo(f4), -1)
        assertEquals(f4.compareTo(f3), 1)
    }
}
