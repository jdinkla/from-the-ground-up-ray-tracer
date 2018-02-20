package net.dinkla.raytracer.utilities

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class ResolutionTest {

    @Test
    fun `calculate hres from vres`() {
        assertEquals(1920, Resolution(1080).hres)
    }

}