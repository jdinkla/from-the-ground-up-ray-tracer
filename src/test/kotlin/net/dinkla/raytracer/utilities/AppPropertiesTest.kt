package net.dinkla.raytracer.utilities

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals

class AppPropertiesTest {

    @Test
    fun get() {
        val a = AppProperties["test.id"] as String?
        assertEquals(a, "4321")
    }

}
