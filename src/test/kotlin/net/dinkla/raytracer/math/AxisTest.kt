package net.dinkla.raytracer.math

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class AxisTest {

    @Test
    operator fun next() {
        assertEquals(Axis.Y, Axis.X.next())
        assertEquals(Axis.Z, Axis.Y.next())
        assertEquals(Axis.X, Axis.Z.next())
    }

    @Test
    fun fromInt() {
        assertEquals(Axis.X, Axis.fromInt(0))
        assertEquals(Axis.Y, Axis.fromInt(1))
        assertEquals(Axis.Z, Axis.fromInt(2))
        assertEquals(Axis.Z, Axis.fromInt(5))
    }
}