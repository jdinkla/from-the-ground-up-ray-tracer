package net.dinkla.raytracer.utilities

import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.gui.extractFileName
import net.dinkla.raytracer.gui.getOutputPngFileName

internal class FilenameUtilitiesTest : StringSpec({

    "getOutputPngFileName #1" {
        val s = getOutputPngFileName("World73.groovy")
        s.substring(s.length - 11) shouldBe "World73.png"
    }

    "getOutputPngFileName #2" {
        val s = getOutputPngFileName("ABC.World73.groovy")
        s.substring(s.length - 11) shouldBe "World73.png"
    }

    "extractFileName should extract filename for Windows directories" {
        val fileName =
            "C:\\workspace\\from-the-ground-up-ray-tracer\\src\\main\\kotlin\\net\\dinkla\\raytracer\\examples\\NewWorld3.kt"
        val directory = "C:\\workspace\\from-the-ground-up-ray-tracer\\src\\main\\kotlin\\net\\dinkla\\raytracer"
        extractFileName(fileName, directory, "\\") shouldBe "examples\\NewWorld3.kt"
    }

    "extractFileName should extract filename for UNIX directories" {
        val fileName =
            "/workspace/from-the-ground-up-ray-tracer/src/main/kotlin/net/dinkla/raytracer/examples/NewWorld3.kt"
        val directory = "/workspace/from-the-ground-up-ray-tracer/src/main/kotlin/net/dinkla/raytracer"
        extractFileName(fileName, directory, "/") shouldBe "examples/NewWorld3.kt"
    }
})