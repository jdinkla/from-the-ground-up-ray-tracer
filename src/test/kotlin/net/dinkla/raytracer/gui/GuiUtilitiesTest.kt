package net.dinkla.raytracer.gui

import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*

internal class GuiUtilitiesTest {

    @Test
    fun `getOutputPngFileName #1`() {
        val s = getOutputPngFileName("World73.groovy")
        assertEquals("World73.png", s.substring(s.length - 11))
    }

    @Test
    fun `getOutputPngFileName #2`() {
        val s = getOutputPngFileName("ABC.World73.groovy")
        assertEquals("World73.png", s.substring(s.length - 11))
    }

    @Test
    fun `extractFileName should extract filename for Windows directories`() {
        assertEquals("examples\\NewWorld3.kt", extractFileName("C:\\workspace\\from-the-ground-up-ray-tracer\\src\\main\\kotlin\\net\\dinkla\\raytracer\\examples\\NewWorld3.kt", "C:\\workspace\\from-the-ground-up-ray-tracer\\src\\main\\kotlin\\net\\dinkla\\raytracer", "\\"))
    }

    @Test
    fun `extractFileName should extract filename for UNIX directories`() {
        assertEquals("examples/NewWorld3.kt", extractFileName("/workspace/from-the-ground-up-ray-tracer/src/main/kotlin/net/dinkla/raytracer/examples/NewWorld3.kt", "/workspace/from-the-ground-up-ray-tracer/src/main/kotlin/net/dinkla/raytracer", "/"))
    }
}