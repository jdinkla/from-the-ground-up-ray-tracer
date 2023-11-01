package net.dinkla.raytracer.utilities

import com.soywiz.klock.DateTime
import com.soywiz.klock.Month
import com.soywiz.klock.Year
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import net.dinkla.raytracer.gui.fileNameWithoutDirectory
import net.dinkla.raytracer.gui.outputPngFileName

internal class FilenameUtilitiesTest : StringSpec({

    val fileNameBase = "World73"
    val timeStamp = DateTime(Year(2022), Month.September, 17, 17, 42, 20, 0)

    "should exchange extension" {
        val s = outputPngFileName("$fileNameBase.kt", timeStamp)
        s shouldBe "20220917174220_$fileNameBase.png"
    }

    "should extract filename from Windows directories" {
        val fileName = "C:\\workspace\\from-the\\kotlin\\net\\dinkla\\raytracer\\examples\\NewWorld3.kt"
        val directory = "C:\\workspace\\from-the\\kotlin\\net\\dinkla\\raytracer"
        fileNameWithoutDirectory(fileName, directory, "\\") shouldBe "examples\\NewWorld3.kt"
    }

    "should extract filename from UNIX directories" {
        val fileName = "/workspace/from-the/kotlin/net/dinkla/raytracer/examples/NewWorld3.kt"
        val directory = "/workspace/from-the/kotlin/net/dinkla/raytracer"
        fileNameWithoutDirectory(fileName, directory, "/") shouldBe "examples/NewWorld3.kt"
    }
})
