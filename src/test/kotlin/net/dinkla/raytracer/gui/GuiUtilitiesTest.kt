package net.dinkla.raytracer.gui

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class GuiUtilitiesTest {

    @Test
    fun `getOutputPngFileName #1`() {
        val s = GuiUtilities.getOutputPngFileName("World73.groovy")
        assertEquals("World73.png", s.substring(s.length - 11))
    }

    @Test
    fun `getOutputPngFileName #2`() {
        val s = GuiUtilities.getOutputPngFileName("ABC.World73.groovy")
        assertEquals("World73.png", s.substring(s.length - 11))
    }

}