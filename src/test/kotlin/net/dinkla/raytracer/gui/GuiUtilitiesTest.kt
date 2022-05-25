package net.dinkla.raytracer.gui

import io.kotest.core.spec.style.AnnotationSpec
import io.kotest.matchers.shouldBe

internal class GuiUtilitiesTest : AnnotationSpec() {

    @Test
    fun `getOutputPngFileName #1`() {
        val s = getOutputPngFileName("World73.groovy")
        s.substring(s.length - 11) shouldBe "World73.png"
    }

    @Test
    fun `getOutputPngFileName #2`() {
        val s = getOutputPngFileName("ABC.World73.groovy")
        s.substring(s.length - 11) shouldBe "World73.png"
    }

    @Test
    fun `extractFileName should extract filename for Windows directories`() {
        val fileName =
            "C:\\workspace\\from-the-ground-up-ray-tracer\\src\\main\\kotlin\\net\\dinkla\\raytracer\\examples\\NewWorld3.kt"
        val directory = "C:\\workspace\\from-the-ground-up-ray-tracer\\src\\main\\kotlin\\net\\dinkla\\raytracer"
        extractFileName(fileName, directory, "\\") shouldBe "examples\\NewWorld3.kt"
    }

    @Test
    fun `extractFileName should extract filename for UNIX directories`() {
        val fileName =
            "/workspace/from-the-ground-up-ray-tracer/src/main/kotlin/net/dinkla/raytracer/examples/NewWorld3.kt"
        val directory = "/workspace/from-the-ground-up-ray-tracer/src/main/kotlin/net/dinkla/raytracer"
        extractFileName(fileName, directory, "/") shouldBe "examples/NewWorld3.kt"
    }
}