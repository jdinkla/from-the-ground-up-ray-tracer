package net.dinkla.raytracer.utilities

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.assertEquals

class AppPropertiesTest {

    @Test
    fun get() {
        val a = AppProperties["test.id"] as String?
        assertEquals(a, "4321")
    }

    @Test
    fun getAsInteger() {
        assertEquals(AppProperties.getAsInteger("test.id"), 4321)
    }

    @Test
    fun getAsDouble() {
        assertEquals(AppProperties.getAsDouble("test.id"), 4321.0)
    }
}
